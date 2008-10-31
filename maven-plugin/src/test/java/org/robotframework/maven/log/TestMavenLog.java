package org.robotframework.maven.log;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.easymock.MockControl;
import org.robotframework.maven.log.MavenLog;
import org.robotframework.maven.testutils.RandomFactory;


public class TestMavenLog extends TestCase {

    private MockControl ctrl;

    private Log mock;

    private Log originalLog;

    protected void setUp() throws Exception {
        super.setUp();
        originalLog = MavenLog.getLog();
    }

    protected void tearDown() throws Exception {
        MavenLog.setLog(originalLog);
    }

    public void testIsEnabledCallsAreDelegated() throws Exception {
        String[] methods = { "isDebugEnabled", "isInfoEnabled",
                "isWarnEnabled", "isErrorEnabled" };
        for (int i = 0; i < methods.length; i++) {
            boolean value = RandomFactory.booleanPrimitive();
            substituteLogWithMockObject();
            delegateAndAssert(methods[i], value);
        }
    }

    public void testLoggingCallsAreDelegated() throws Exception {
        String[] methods = { "debug", "info", "warn", "error" };
        Class[][] argTypes = { { CharSequence.class },
                { Throwable.class },
                { CharSequence.class, Throwable.class } };
        for (int m = 0; m < methods.length; m++) {
            for (int a = 0; a < argTypes.length; a++) {
                substituteLogWithMockObject();
                delegateAndAssert(methods[m], argTypes[a]);
            }
        }
    }

    private void delegateAndAssert(String method, Class[] argTypes)
            throws Exception {
        Object[] args = instancesOf(argTypes);
        invokeMethodOnMockObject(method, argTypes, args);
        invokeMethod(method, argTypes, args);
    }

    private void delegateAndAssert(String method, boolean returnValue)
            throws Exception {
        invokeMethodOnMockObject(method, null, null);
        ctrl.setReturnValue(returnValue);
        Object actual = invokeMethod(method, null, null);
        assertEquals(new Boolean(returnValue), actual);
    }

    private void invokeMethodOnMockObject(String name,
            Class[] argTypes, Object[] args) throws Exception {
        Method m = Log.class.getMethod(name, argTypes);
        m.invoke(mock, args);
    }

    private Object invokeMethod(String name, Class[] argTypes,
            Object[] args) throws Exception {
        Method m = MavenLog.class.getMethod(name, argTypes);
        ctrl.replay();
        Object returnedValue = m.invoke(null, args);
        ctrl.verify();
        return returnedValue;
    }

    private void substituteLogWithMockObject() {
        ctrl = MockControl.createControl(Log.class);
        mock = (Log) ctrl.getMock();
        MavenLog.setLog(mock);
    }

    private Object[] instancesOf(Class[] argTypes) {
        Object[] instances = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            instances[i] = RandomFactory.instanceOf(argTypes[i]);
        }
        return instances;
    }
}
