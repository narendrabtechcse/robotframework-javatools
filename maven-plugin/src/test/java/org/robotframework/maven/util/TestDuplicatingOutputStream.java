package org.robotframework.maven.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.robotframework.maven.util.DuplicatingOutputStream;

import junit.framework.TestCase;

public class TestDuplicatingOutputStream extends TestCase {

    public void testOutputGoesToBothStreams() throws Exception {
        ByteArrayOutputStream one = new ByteArrayOutputStream();
        ByteArrayOutputStream two = new ByteArrayOutputStream();
        DuplicatingOutputStream dupe = new DuplicatingOutputStream(
                one, two);
        PrintStream out = new PrintStream(dupe);
        String msg = "Hello";
        out.print(msg);
        assertEquals(msg, one.toString());
        assertEquals(msg, two.toString());
    }
}
