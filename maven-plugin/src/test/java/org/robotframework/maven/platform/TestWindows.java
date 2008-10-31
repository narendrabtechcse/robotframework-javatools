package org.robotframework.maven.platform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.platform.Windows;

import junit.framework.TestCase;

public class TestWindows extends TestCase {

    private Platform platform;

    private String scriptFile;

    private List command;

    private List args;

    protected void setUp() throws Exception {
        super.setUp();
        platform = new Windows();
        scriptFile = new File("path/to/pybot").getPath();
        command = new ArrayList();
        args = new ArrayList();
    }

    protected void tearDown() throws Exception {
        Platform.envOutput = null;
        super.tearDown();
    }

    public void testScriptPrefix() throws Exception {
        platform.addScriptPrefix(command);
        assertEquals(2, command.size());
        assertEquals("cmd.exe", command.get(0));
        assertEquals("/C", command.get(1));
    }

    public void testBuildingScriptCommandWhenThereIsNoArguments()
            throws Exception {
        platform.addScriptAndArguments(command, scriptFile, args);
        List expected = new ArrayList();
        expected.add(scriptFile);
        expected.addAll(args);
        assertEquals(expected, command);
    }

    public void testBuildingScriptCommandWhenThereAreArguments()
            throws Exception {
        args.add("--foo");
        args.add("bar");
        args.add("--quoted-arg");
        args.add("This arg has 'single' and \"double\" quotes");
        platform.addScriptAndArguments(command, scriptFile, args);
        List expected = new ArrayList();
        expected.add(scriptFile);
        expected.addAll(args);
        assertEquals(expected, command);
    }

    public void testReadingEnvironmentVariables() throws Exception {
        platform = new Windows() {
            protected Execution execute(List command,
                    File workingDirectory) throws IOException {
                assertEquals("cmd.exe", command.get(0));
                assertEquals("/C", command.get(1));
                assertEquals("set", command.get(2));
                return new Execution(null) {
                    public InputStream getStderr() {
                        return new ByteArrayInputStream("".getBytes());
                    }

                    public InputStream getStdout() {
                        return new ByteArrayInputStream(
                                "FOO1=BAR1\r\nFOO2=BAR2".getBytes());
                    }
                };
            }
        };
        assertEquals("BAR1", platform.getEnvironmentVariable("FOO1"));
        assertEquals("BAR2", platform.getEnvironmentVariable("FOO2"));
        assertEquals("", platform
                .getEnvironmentVariable("NO_SUCH_VAR"));
    }
}
