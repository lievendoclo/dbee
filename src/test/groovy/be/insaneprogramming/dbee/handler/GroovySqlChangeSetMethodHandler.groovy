package be.insaneprogramming.dbee.handler

import groovy.sql.Sql

import javax.sql.DataSource
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class GroovySqlChangeSetMethodHandler implements ChangeSetMethodHandler {

    @Override
    Object invoke(Method changeSetMethod, Object changeLogInstance, DataSource db)
            throws InvocationTargetException, IllegalAccessException {
        return changeSetMethod.invoke(changeLogInstance, new Sql(db));
    }

    @Override
    boolean canHandle(Method changeSetMethod) {
        return changeSetMethod.parameterTypes.length == 1 && changeSetMethod.parameterTypes[0].equals(Sql.class);
    }
}
