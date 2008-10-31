package org.robotframework.maven.platform;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.util.Grep;
import org.robotframework.maven.util.IO;
import org.robotframework.maven.util.Join;

import junit.framework.TestCase;


public class TestExecution extends TestCase {

    private List cmd;

    private PrintStream systemOutBefore;

    private PrintStream systemErrBefore;

    private File systemOutFile;

    private File systemErrFile;

    protected void setUp() throws Exception {
        super.setUp();

        this.systemOutBefore = System.out;
        this.systemErrBefore = System.err;

        systemOutFile = File.createTempFile("stdout", ".txt");
        systemErrFile = File.createTempFile("stderr", ".txt");
        System.setOut(new PrintStream(systemOutFile));
        System.setErr(new PrintStream(systemErrFile));

        cmd = new ArrayList();
        cmd.add("echo");
        cmd.add("foo");
    }

    protected void tearDown() throws Exception {
        System.setOut(systemOutBefore);
        System.setErr(systemErrBefore);
        super.tearDown();
    }

    private Execution executeAndWaitForCommand() {
        Execution execution = new Execution(cmd);
        execution.run();
        execution.waitForCompletion();
        return execution;
    }

    public void testExecutionOutputCanBeObtainedAfterwards()
            throws Exception {
        Execution execution = executeAndWaitForCommand();
        String stdout = IO.readToString(execution.getStdout());
        assertEquals("foo", stdout.trim());
    }

    public void testConsoleOutputIsDisabledByDefault()
            throws Exception {
        Execution execution = executeAndWaitForCommand();
        File stdout = execution.getStdoutFile();

        assertFileContains(stdout, "foo");
        assertFileDoesNotContain(systemOutFile, "foo");
    }

    public void testSystemOutOfLaunchedProcessCanBeCopiedToCallersSystemOut()
            throws Exception {
        Execution execution = new Execution(cmd);
        execution.setConsoleOutputEnabled(true);
        execution.run();
        execution.waitForCompletion();
        File stdout = execution.getStdoutFile();
        assertEquals(readNonMavenLogLinesFrom(systemOutFile),
                readNonMavenLogLinesFrom(stdout));
    }

    private void assertFileContains(File file, String expected)
            throws IOException {
        String output = readNonMavenLogLinesFrom(file);
        assertTrue("Output should've ended up in "
                + file.getAbsolutePath() + ":\n" + output, output
                .indexOf("foo") >= 0);
    }

    private void assertFileDoesNotContain(File file, String expected)
            throws IOException {
        String output = readNonMavenLogLinesFrom(file);
        assertTrue("Output should not have ended up in "
                + file.getAbsolutePath() + ":\n" + output, output
                .indexOf("foo") < 0);
    }

    private String readNonMavenLogLinesFrom(File stdout)
            throws IOException {
        String rawFileContent = IO.readToString(stdout);
        String[] nonMavenLogLines = Grep.pattern("[^\\[].*").from(
                rawFileContent);
        return Join.lines(nonMavenLogLines).with("\n");
    }
}
