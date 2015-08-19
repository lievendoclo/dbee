package be.insaneprogramming.dbee.utils;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import be.insaneprogramming.dbee.annotation.ChangeLog;
import be.insaneprogramming.dbee.annotation.ChangeSet;
import be.insaneprogramming.dbee.model.ChangeEntry;

import static java.util.Arrays.asList;

/**
 * Utilities to deal with reflections and annotations
 *
 * @author lstolowski
 * @since 27/07/2014
 */
public class ChangeService {

    private final String changeLogsBasePackage;

    public ChangeService() {
        this(null);
    }

    public ChangeService(String changeLogsBasePackage) {
        this.changeLogsBasePackage = changeLogsBasePackage;
    }


    public List<Class<?>> fetchChangeLogs() {
        Reflections reflections = new Reflections(changeLogsBasePackage);
        List<Class<?>> changeLogs = new ArrayList<>(reflections.getTypesAnnotatedWith(ChangeLog.class));

        Collections.sort(changeLogs, new ChangeLogComparator());

        return changeLogs;
    }

    public List<Method> fetchChangeSets(final Class<?> type) {
        final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
        Collections.sort(changeSets, new ChangeSetComparator());
        return changeSets;
    }

    public boolean isRunAlwaysChangeSet(Method changesetMethod) {
        ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);
        return annotation.runAlways();
    }

    public boolean isFailOnErrorChangeSet(Method changesetMethod) {
        ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);
        return annotation.failOnError();
    }

    public ChangeEntry createChangeEntry(Method changesetMethod) {
        ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);
        return new ChangeEntry(
            annotation.author(),
            new Date().getTime(),
            changesetMethod.getDeclaringClass().getName(),
            changesetMethod.getName());
    }

    private List<Method> filterChangeSetAnnotation(List<Method> allMethods) {
        final List<Method> changesetMethods = new ArrayList<>();
        for (final Method method : allMethods) {
            if (method.isAnnotationPresent(ChangeSet.class)) {
                changesetMethods.add(method);
            }
        }
        return changesetMethods;
    }

}
