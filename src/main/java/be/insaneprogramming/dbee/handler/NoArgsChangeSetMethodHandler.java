package be.insaneprogramming.dbee.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

public class NoArgsChangeSetMethodHandler implements ChangeSetMethodHandler {

    @Override
    public Object invoke(Method changeSetMethod, Object changeLogInstance, DataSource db)
        throws InvocationTargetException, IllegalAccessException {
        return changeSetMethod.invoke(changeLogInstance);
    }

    @Override
    public boolean canHandle(Method changeSetMethod) {
        return changeSetMethod.getParameterTypes().length == 0;
    }


}