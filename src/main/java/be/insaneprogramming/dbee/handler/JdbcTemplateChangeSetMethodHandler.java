package be.insaneprogramming.dbee.handler;

import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

public class JdbcTemplateChangeSetMethodHandler implements ChangeSetMethodHandler {

    @Override
    public Object invoke(Method changeSetMethod, Object changeLogInstance, DataSource db)
        throws InvocationTargetException, IllegalAccessException {
        return changeSetMethod.invoke(changeLogInstance, new JdbcTemplate(db));
    }

    @Override
    public boolean canHandle(Method changeSetMethod) {
        return changeSetMethod.getParameterTypes().length == 1
               && changeSetMethod.getParameterTypes()[0].equals(JdbcTemplate.class);
    }


}
