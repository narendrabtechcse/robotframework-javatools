package org.robotframework.javalib.reflection;

import junit.framework.TestCase;

import org.robotframework.javalib.util.ArrayUtil;

public class ArgumentGrouperTest extends TestCase {
    private String[] providedArguments = new String[] { "arg1", "arg2", "arg3", "arg4", "arg5", "arg6" };
    private Class[] argumentTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };

    public void testReturnsOriginalArgumentsIfArgumentCountMatches() throws Exception {
        IArgumentGrouper grouper = new ArgumentGrouper(argumentTypes);
        ArrayUtil.assertArraysEquals(providedArguments, grouper.groupArguments(providedArguments));
    }

    public void testReturnsOriginalArgumentsIfTheyAreNull() throws Exception {
        IArgumentGrouper grouper = new ArgumentGrouper(argumentTypes);
        assertNull(grouper.groupArguments(null));
    }

    public void testGroupsArgumentsToMatchTheActualArgumentCount() throws Exception {
        for (int i = 1; i <= providedArguments.length; i++) {
            assertArrayLengthMatches(i);
        }
    }

    public void testStacksAllProvidedArgumentsIfThereIsOnlyOneActualArgument() throws Exception {
        Object[] groupedArguments = new ArgumentGrouper(new Class[] { String.class }).groupArguments(providedArguments);
        ArrayUtil.assertArraysEquals(providedArguments, (Object[]) groupedArguments[0]);
    }

    public void testStacksExcessArguments() throws Exception {
        for (int i = 1; i < providedArguments.length; i++) {
            assertGroupedCorrectlyWhenActualArgumentCountIs(i);
        }
    }

    public void testStacksArgumentsIfLastArgumentIsOfArrayType() throws Exception {
        Class[] types = new Class[] { String.class, String[].class };
        Object[] groupedArguments = new ArgumentGrouper(types).groupArguments(new String[] { "arg1", "arg2" });
        assertEquals("arg1", groupedArguments[0]);
        ArrayUtil.assertArraysEquals(new String[] { "arg2" }, (String[])groupedArguments[1]);
    }

    public void testCanBeCalledWithoutArgumentsIfLastArgumentIsOfArrayType() throws Exception {
        Class[] types = new Class[] { String.class, String[].class };
        Object[] groupedArguments = new ArgumentGrouper(types).groupArguments(new String[] { "arg1" });
        assertEquals("arg1", groupedArguments[0]);
        ArrayUtil.assertArraysEquals(new String[0], (String[])groupedArguments[1]);
    }

    private void assertGroupedCorrectlyWhenActualArgumentCountIs(int actualArgCount) {
        Object[] groupedArguments = new ArgumentGrouper(createTypes(actualArgCount)).groupArguments(providedArguments);
        for (int i = 0; i < actualArgCount - 1; i++) {
            assertEquals(providedArguments[i], groupedArguments[i]);
        }

        assertArgumentsAreStackedCorrectly(groupedArguments, actualArgCount - 1);
    }

    private Class[] createTypes(int actualArgCount) {
        Class[] types = new Class[actualArgCount];
        for (int i = 0; i < types.length; i++) {
            types[i] = String.class;
        }
        return types;
    }

    private void assertArgumentsAreStackedCorrectly(Object[] groupedArguments, int stackStartIndex) {
        String[] expectedStackedArguments = ArrayUtil.copyOfRange(providedArguments, stackStartIndex, providedArguments.length);
        Object[] actualStackedArguments = (Object[]) groupedArguments[stackStartIndex];

        ArrayUtil.assertArraysEquals(expectedStackedArguments, actualStackedArguments);
    }

    private void assertArrayLengthMatches(int actualArgumentCount) {
        int groupedArgumentCount = new ArgumentGrouper(createTypes(actualArgumentCount)).groupArguments(providedArguments).length;
        assertEquals(actualArgumentCount, groupedArgumentCount);
    }
}
