package org.robotframework.maven.platform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.platform.Unix;

import junit.framework.TestCase;

public class TestUnix extends TestCase {

    private Platform platform;

    private String scriptFile;

    private List command;

    private List args;

    protected void setUp() throws Exception {
        super.setUp();
        platform = new Unix();
        scriptFile = new File("path/to/pybot").getPath();
        command = new ArrayList();
        args = new ArrayList();
    }

    protected void tearDown() throws Exception {
        Platform.envOutput = null;
        super.tearDown();
    }

    public void testScriptPrefix() throws Exception {
        platform.runScript(scriptFile, args);
        platform.addScriptPrefix(command);
        assertEquals(2, command.size());
        assertEquals("sh", command.get(0));
        assertEquals("-c", command.get(1));
    }

    public void testBuildingScriptCommandWhenThereIsNoArguments()
            throws Exception {
        platform.addScriptAndArguments(command, scriptFile, args);
        assertEquals(1, command.size());
        assertEquals(scriptFile, command.get(0));
    }

    public void testBuildingScriptCommandWhenThereAreArguments()
            throws Exception {
        args.add("--foo");
        args.add("bar");
        platform.addScriptAndArguments(command, scriptFile, args);
        assertEquals(1, command.size());
        assertEquals(scriptFile + " --foo \"bar\"", command.get(0));
    }

    public void testReadingEnvironmentVariables() throws Exception {
        platform = new Unix() {
            protected Execution execute(List command,
                    File workingDirectory) throws IOException {
                assertEquals("env", command.get(0));
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
