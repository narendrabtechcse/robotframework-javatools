package org.robotframework.maven.util;

public class ID {
    
    private static long counter = 1;

    public static synchronized long next() {
        return counter++;
    }
}
