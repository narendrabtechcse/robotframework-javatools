package org.robotframework.maven.testutils;

import java.util.HashMap;
import java.util.Map;

public class RandomFactory {

    private static final Map implementations = new HashMap();
    static {
        implementations.put(CharSequence.class, String.class);
        implementations.put(Throwable.class, RuntimeException.class);
    }

    public static boolean booleanPrimitive() {
        return Math.random() > 0.5 ? true : false;
    }

    public static Boolean booleanObject() {
        return new Boolean(booleanPrimitive());
    }

    public static Object instanceOf(Class klass) {
        if (!implementations.containsKey(klass)) {
            System.err.println("WARNING: "
                    + "don't know how to create an instance of "
                    + klass.getName());
            return null;
        }
        try {
            return ((Class) implementations.get(klass)).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
