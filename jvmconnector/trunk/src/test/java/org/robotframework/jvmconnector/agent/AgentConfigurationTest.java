package org.robotframework.jvmconnector.agent;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class AgentConfigurationTest{
    @Test
    public void parsePortFromBeginning() {
        testParser("Port=1234:foo.jar", 1234, "foo.jar");
    }

    @Test
    public void parsePortFromEnd() {
        testParser("foo.jar:port=1234", 1234, "foo.jar");
    }

    @Test
    public void parsePortFromMiddle() {
        testParser("zip.jar:PORT=1234:foo.jar", 1234, "zip.jar:foo.jar");
    }

    @Test
    public void parseWithoutPort() {
        testParser("zip.jar:foo.jar", null, "zip.jar:foo.jar");
    }

    @Test
    public void parseWithInvalidPort() {
        try {
        	testParser("zip.jar:port=abcd:foo.jar", null, "zip.jar:foo.jar");
        	throw new AssertionError("Not numeric port should fail");
        }
        catch (NumberFormatException error) {}
    }

    private void testParser(String input, Integer port, String jars) {
        AgentConfiguration conf = new AgentConfiguration(input.replaceAll(":", 
                File.pathSeparator));
        assertEquals(port, conf.getPort());
        List<String> expected = Arrays.asList(jars.split(":"));
        assertEquals(expected, conf.getJars());
    }
}