package org.robotframework.maven.util;

import java.util.Arrays;

import org.robotframework.maven.util.Grep;

import junit.framework.TestCase;

public class TestGrep extends TestCase {

    public void testExactMatch() throws Exception {
        String[] matches = Grep.exact("exact").from(
                "one\nexact\ntwo\nagain exact\nexactly!");
        String[] expected = { "exact", "again exact", "exactly!" };
        assertEquals(Arrays.asList(expected), Arrays.asList(matches));
    }

    public void testPatternMatching() throws Exception {
        String[] matches = Grep.pattern("(e+)dle?").from(
                "edle\nneedle\ndle\nedl\nedlee");
        String[] expected = { "edle", "edl" };
        assertEquals(Arrays.asList(expected), Arrays.asList(matches));
    }
}
