package be.insaneprogramming.dbee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import be.insaneprogramming.dbee.dao.ChangeEntryDao;
import be.insaneprogramming.dbee.exception.DBeeChangeSetException;
import be.insaneprogramming.dbee.exception.DBeeChangeSetExecutionException;
import be.insaneprogramming.dbee.exception.DBeeException;
import be.insaneprogramming.dbee.handler.ChangeSetMethodHandler;
import be.insaneprogramming.dbee.handler.DataSourceChangeSetMethodHandler;
import be.insaneprogramming.dbee.handler.JdbcTemplateChangeSetMethodHandler;
import be.insaneprogramming.dbee.handler.NoArgsChangeSetMethodHandler;
import be.insaneprogramming.dbee.model.ChangeEntry;
import be.insaneprogramming.dbee.utils.ChangeLogComparator;
import be.insaneprogramming.dbee.utils.ChangeService;

public class DBee {

    private static final Logger logger = LoggerFactory.getLogger(DBee.class);

    private ChangeEntryDao dao;

    private boolean enabled = true;
    private String changeLogsScanPackage;
    private List<Class<?>> changeLogClasses;
    private DataSource dataSource;

    private Set<Class<? extends ChangeSetMethodHandler>> changeSetMethodHandlers;

    public DBee(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new ChangeEntryDao(new JdbcTemplate(dataSource));
        changeSetMethodHandlers = new HashSet<>();
        addDefaultChangeSetMethodHandlers();
    }

    private void addDefaultChangeSetMethodHandlers() {
        changeSetMethodHandlers.add(NoArgsChangeSetMethodHandler.class);
        changeSetMethodHandlers.add(DataSourceChangeSetMethodHandler.class);
        changeSetMethodHandlers.add(JdbcTemplateChangeSetMethodHandler.class);
    }

    public void execute() throws DBeeException {
        if (!isEnabled()) {
            logger.info("DBee is disabled. Exiting.");
            return;
        }

        logger.info("DBee has started the data migration sequence..");

        Set<Class<?>> changeLogClassSet = new HashSet<>();
        if (changeLogClasses != null) {
            changeLogClassSet.addAll(changeLogClasses);
        }
        ChangeService service;
        if (changeLogsScanPackage != null) {
            service = new ChangeService(changeLogsScanPackage);
            changeLogClassSet.addAll(service.fetchChangeLogs());
        } else {
            service = new ChangeService();
        }
        List<Class<?>> sortedChangeLogClasses = new ArrayList<>(changeLogClassSet);
        Collections.sort(sortedChangeLogClasses, new ChangeLogComparator());

        for (Class<?> changelogClass : sortedChangeLogClasses) {

            Object changelogInstance;
            try {
                changelogInstance = changelogClass.getConstructor().newInstance();
                List<Method> changesetMethods = service.fetchChangeSets(changelogInstance.getClass());

                for (Method changesetMethod : changesetMethods) {
                    ChangeEntry changeEntry = service.createChangeEntry(changesetMethod);

                    try {
                        if (dao.isNewChange(changeEntry)) {
                            try {
                                executeChangeSetMethod(changesetMethod, changelogInstance, dataSource);
                                logger.info(changeEntry + " applied");
                                dao.save(changeEntry);
                            } catch(DBeeChangeSetExecutionException ex) {
                                if (service.isFailOnErrorChangeSet(changesetMethod)) {
                                    throw ex;
                                } else {
                                    logger.info(changeEntry + " threw exception but was ignored");
                                }
                            }
                        } else if (service.isRunAlwaysChangeSet(changesetMethod)) {
                            try {
                                executeChangeSetMethod(changesetMethod, changelogInstance, dataSource);
                                logger.info(changeEntry + " reapplied");
                            } catch(DBeeChangeSetExecutionException ex) {
                                if (service.isFailOnErrorChangeSet(changesetMethod)) {
                                    throw ex;
                                } else {
                                    logger.info(changeEntry + " threw exception but was ignored");
                                }
                            }
                        } else {
                            logger.info(changeEntry + " passed over");
                        }
                    } catch (DBeeChangeSetException e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new DBeeException(e.getMessage(), e);
            }

        }
        logger.info("DBee has finished his job.");
    }

    private Object executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance, DataSource db)
        throws IllegalAccessException, InvocationTargetException, DBeeChangeSetException {
        for (Class<? extends ChangeSetMethodHandler> handlerClass : changeSetMethodHandlers) {
            ChangeSetMethodHandler handler;
            try {
                handler = handlerClass.newInstance();
                if (handler.canHandle(changeSetMethod)) {
                    try {
                        return handler.invoke(changeSetMethod, changeLogInstance, db);
                    } catch (Exception ex) {
                        throw new DBeeChangeSetExecutionException("Error executing changeset: " + ex.getMessage(), ex);
                    }
                }
            } catch (InstantiationException e) {
                throw new DBeeChangeSetException("Could not instantiate changeset method handler, should have empty constructor");
            }
        }
        throw new DBeeChangeSetException("ChangeSet method " + changeSetMethod.getName() +
                                         " has no handlers. Please see docs for more info!");
    }

    /**
     * Package name where @ChangeLog-annotated classes are kept.
     *
     * @param changeLogsScanPackage package where your changelogs are
     * @return DBee object for fluent interface
     */
    public DBee setChangeLogsScanPackage(String changeLogsScanPackage) {
        this.changeLogsScanPackage = changeLogsScanPackage;
        return this;
    }

    public void setChangeLogClasses(List<Class<?>> changeLogClasses) {
        this.changeLogClasses = changeLogClasses;
    }

    /**
     * @return true if DBee runner is enabled and able to run, otherwise false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Feature which enables/disables DBee runner execution
     *
     * @param enabled DBee will run only if this option is set to true
     * @return DBee object for fluent interface
     */
    public DBee setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void addChangeSetMethodHandler(Class<? extends ChangeSetMethodHandler> handlerClass) {
        changeSetMethodHandlers.add(handlerClass);
    }
}
