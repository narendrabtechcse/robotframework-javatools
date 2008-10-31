package org.robotframework.maven.platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.platform.Unix;
import org.robotframework.maven.platform.Windows;
import org.robotframework.maven.testutils.DummyMain;
import org.robotframework.maven.util.ClasspathBuilder;

import junit.framework.TestCase;


/**
 * @author Lasse Koskela
 */
public class TestPlatform extends TestCase {

    private String classpath;

    private String originalOsName;

    private FakePlatform platform;

    protected void setUp() throws Exception {
        super.setUp();
        ClasspathBuilder builder = new ClasspathBuilder(true);
        builder.add("target/classes");
        builder.add("target/test-classes");
        classpath = builder.toString();

        originalOsName = System.getProperty("os.name");

        platform = new FakePlatform();
        platform.setupEnvironmentVariable("propagated1",
                "propagatedValue1");
        platform.setupEnvironmentVariable("propagated2",
                "propagatedValue2");
        platform.setupEnvironmentVariable("propagated3",
                "propagatedValue3");
    }

    protected void tearDown() throws Exception {
        System.setProperty("os.name", originalOsName);
        super.tearDown();
    }

    public void testResolvingThePlatform() throws Exception {
        String[] unixes = { "AIX", "Digital Unix", "FreeBSD",
                "HP UX", "HP-UX", "Irix", "Linux", "Mac OS",
                "Mac OS X", "Solaris", "SunOS" };
        for (int i = 0; i < unixes.length; i++) {
            System.setProperty("os.name", unixes[i]);
            assertEquals(Unix.class, Platform.resolve().getClass());
        }
        String[] windowses = { "Windows 2000", "Windows 95",
                "Windows 98", "Windows Me", "Windows NT",
                "Windows XP", "Windows 2003" };
        for (int i = 0; i < windowses.length; i++) {
            System.setProperty("os.name", windowses[i]);
            assertEquals(Windows.class, Platform.resolve().getClass());
        }
    }

    public void testInvokingSystemCommands() throws Exception {
        String binary = new File(System.getProperty("java.home"),
                "bin/java").getAbsolutePath();
        String[] arguments = { "-classpath", classpath,
                DummyMain.class.getName() };
        Execution e = Platform.resolve().runExecutable(binary,
                Arrays.asList(arguments));
        assertEquals(DummyMain.EXIT_VALUE, e.getExitCode());
    }

    public void testOverloadedRunScriptMethodsDelegateToThreeArgVersion()
            throws Exception {
        final Execution expectedExecution = new Execution(null);
        final String expectedScript = "script";
        final List expectedArgs = Collections.singletonList("arg");
        final File expectedDir = new File(".");

        assertSame(expectedExecution, new FakePlatform() {
            public Execution runScript(String file, List args,
                    File workingDirectory) throws IOException {
                assertEquals(expectedScript, file);
                assertEquals(expectedArgs, args);
                assertEquals(expectedDir.getAbsolutePath(),
                        workingDirectory.getAbsolutePath());
                return expectedExecution;
            }
        }.runScript(expectedScript, expectedArgs));

        assertSame(expectedExecution, new FakePlatform() {
            public Execution runScript(String file, List args,
                    File workingDirectory) throws IOException {
                assertEquals(expectedScript, file);
                assertEquals(new ArrayList(), args);
                assertEquals(expectedDir.getAbsolutePath(),
                        workingDirectory.getAbsolutePath());
                return expectedExecution;
            }
        }.runScript(expectedScript));
    }

    public void testExecutedCommandIncludesPlatformSpecificPrefixAndTheGivenArguments()
            throws Exception {
        final String givenScript = "script";
        final List givenArgs = Collections.singletonList("givenArg");
        final List expectedCommand = new ArrayList();
        expectedCommand.add(platform.scriptPrefix);
        expectedCommand.add(givenScript);
        expectedCommand.addAll(givenArgs);
        platform.runScript(givenScript, givenArgs);
        assertEquals(expectedCommand, platform.executedCommand);
    }

    public void testEnvironmentVariablesCanBeConfigured()
            throws Exception {
        platform.setEnvironmentVariable("name1", "value1");
        platform.setEnvironmentVariable("name2", "value2");
        Execution execution = platform.runScript("script");
        List values = Arrays.asList(execution.environmentVariables);
        assertTrue(values.contains("name1=value1"));
        assertTrue(values.contains("name2=value2"));
    }

    public void testEnvironmentVariablesCanBePropagated()
            throws Exception {
        platform.propagateEnvironmentVariable("propagated1");
        platform.propagateEnvironmentVariable("propagated2");
        Execution execution = platform.runScript("script");
        List values = Arrays.asList(execution.environmentVariables);
        assertTrue(values.contains("propagated1=propagatedValue1"));
        assertTrue(values.contains("propagated2=propagatedValue2"));
        assertFalse(values.contains("propagated3=propagatedValue3"));
    }
}
