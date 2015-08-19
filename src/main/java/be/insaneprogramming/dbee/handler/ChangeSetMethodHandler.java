package be.insaneprogramming.dbee.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

public interface ChangeSetMethodHandler {
    Object invoke(Method changeSetMethod, Object changeLogInstance, DataSource db) throws InvocationTargetException, IllegalAccessException;
    boolean canHandle(Method changeSetMethod);
}
