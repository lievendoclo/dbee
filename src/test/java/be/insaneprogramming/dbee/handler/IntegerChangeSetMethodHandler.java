package be.insaneprogramming.dbee.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

public class IntegerChangeSetMethodHandler implements ChangeSetMethodHandler {

    @Override
    public Object invoke(Method changeSetMethod, Object changeLogInstance, DataSource db)
        throws InvocationTargetException, IllegalAccessException {
        return changeSetMethod.invoke(changeLogInstance, 5);
    }

    @Override
    public boolean canHandle(Method changeSetMethod) {
        return changeSetMethod.getParameterTypes().length == 1
               && changeSetMethod.getParameterTypes()[0].equals(Integer.class);
    }
}
