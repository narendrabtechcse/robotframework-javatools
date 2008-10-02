package org.robotframework.javalib.reflection;

import java.lang.reflect.Method;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.laughingpanda.beaninject.Inject;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.util.ArrayUtil;

import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

public class KeywordInvokerTest extends MockObjectTestCase {
    private Mock paranamer;
    private IKeywordInvoker keywordInvoker;
    private Method keywordMethod;
    private Object[] restOfArgs;

    protected void setUp() throws Exception {
        keywordInvoker = createKeywordInvoker("someMethod");
    }

    public void testReturnsParameterNamesFromArgumentAnnotation() throws Exception {
        IKeywordInvoker invoker = createKeywordInvoker("keywordWithArgumentAnnotation");
        paranamer.expects(never()).method("lookupParameterNames");
        ArrayUtil.assertArraysEquals(new String[] {"firstArg", "*args"}, invoker.getParameterNames());
    }

    public void testReturnsParameterNamesFromParameterInformationIfArgumentAnnotationIsNotPresent() throws Exception {
        String[] parameterNames = new String [] { "parameter1", "parameter2" };

        paranamer.expects(once()).method("lookupParameterNames")
            .with(eq(keywordMethod))
            .will(returnValue(parameterNames));

        ArrayUtil.assertArraysEquals(parameterNames, keywordInvoker.getParameterNames());
    }

    public void testReturnsNullIfArgumentAnnotationAndParameterNameInformationIsNotPresent() throws Exception {
        paranamer.expects(once()).method("lookupParameterNames")
            .with(eq(keywordMethod))
            .will(throwException(new ParameterNamesNotFoundException("not found")));

        assertNull(keywordInvoker.getParameterNames());
    }

    public void testInvokesWrappedMethod() throws Exception {
        Object[] args = new String[] { "someArg", "moreArgs" };
        assertEquals("someArg", keywordInvoker.invoke(args));
    }

    public void testGroupsRestOfTheArgumentsIfProvidedArgumentCountIsGreaterThanActualArgumentCount() throws Exception {
        Object[] args = new String[] { "arg1", "arg2", "arg3" };
        Object[] expectedRestOfArgs = new Object[] { "arg1", new String[] { "arg2", "arg3" }};

        final Mock argumentGrouper = mock(IArgumentGrouper.class);
        argumentGrouper.expects(once()).method("groupArguments")
            .with(same(args))
            .will(returnValue(expectedRestOfArgs));

        IKeywordInvoker wrapperWithMockGrouper = new KeywordInvoker(this, keywordMethod) {
            IArgumentGrouper createArgumentGrouper() {
                return (IArgumentGrouper) argumentGrouper.proxy();
            }
        };

        wrapperWithMockGrouper.invoke(args);
        ArrayUtil.assertArraysEquals((Object[]) expectedRestOfArgs[1], restOfArgs);
    }

    public void testThrowsRuntimeExceptionInCaseOfException() throws Exception {
        try {
            keywordInvoker.invoke(null);
            fail();
        } catch (RuntimeException e) {
            //Expected
        }
    }

    public void testGetsAnnotationValue() throws Exception {
        assertEquals("documentation", keywordInvoker.getAnnotationValue(RobotKeyword.class));
    }

    private IKeywordInvoker createKeywordInvoker(String methodName) throws NoSuchMethodException {
        keywordMethod = this.getClass().getMethod(methodName, String.class, String[].class);
        keywordInvoker = new KeywordInvoker(this, keywordMethod);
        paranamer = mock(Paranamer.class);
        Inject.field("parameterNames").of(keywordInvoker).with(paranamer.proxy());
        return keywordInvoker;
    }

    @RobotKeyword("documentation")
    public String someMethod(String arg, String[] restOf) {
        this.restOfArgs = restOf;
        return (String) arg;
    }

    @ArgumentNames({"firstArg", "*args"})
    @RobotKeyword("documentation")
    public void keywordWithArgumentAnnotation(String arg, String[] restOf) {
    }
}
