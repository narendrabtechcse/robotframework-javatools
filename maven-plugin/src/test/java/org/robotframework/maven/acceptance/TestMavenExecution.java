package org.robotframework.maven.acceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.testutils.MavenInstallation;
import org.robotframework.maven.testutils.MavenProjectTemplate;
import org.robotframework.maven.testutils.RobotInstallation;
import org.robotframework.maven.testutils.TempDir;
import org.robotframework.maven.util.Grep;
import org.robotframework.maven.util.IO;

import junit.framework.TestCase;


/**
 * @author Lasse Koskela
 */
public class TestMavenExecution extends TestCase {

    private MavenProjectTemplate project;

    private File projectRoot;

    private String stdout;

    private String stderr;

    private String robotStdout;

    private String robotStderr;

    private static final File localRepository = new File(System
            .getProperty("user.home"), ".m2/repository");

    protected void setUp() throws Exception {
        super.setUp();
        project = new MavenProjectTemplate(
                "src/test/resources/template.pom.xml");
        project.robotScript = RobotInstallation.pathToPybot();
        projectRoot = TempDir.create();
    }

    public void testSuccessfulBuild() throws Exception {
        executeRobotTestGoal();
        assertStdoutIncludes("Robot returned with exit code 0");
        assertStdoutIncludes("[INFO] BUILD SUCCESSFUL");
        assertClasspathIncludes(projectRoot, "target/classes");
        assertClasspathIncludes(
                localRepository,
                "commons-collections/commons-collections/3.2/commons-collections-3.2.jar");
    }

    public void testBuildFailsIfRobotTestsFail() throws Exception {
        project.robotArguments.add("--variable");
        String interpreter = "thisShouldFailTheRobotTest";
        project.robotArguments.add("INTERPRETER:" + interpreter);
        executeRobotTestGoal();
        assertStdoutIncludes("but given interpreter was '"
                + interpreter + "'");
        assertStdoutIncludes("Robot returned with exit code 1");
        assertStdoutIncludes("[ERROR] BUILD FAILURE");
    }

    private void assertClasspathIncludes(File parent, String path) {
        assertClasspathIncludes(new File(parent, path)
                .getAbsolutePath());
    }

    private void assertClasspathIncludes(String path) {
        String[] lines = Grep
                .exact("initialized Robot class path to")
                .from(stdout);
        assertTrue("No line listing the classpathElements was "
                + "found in Maven output:\n\n" + stdout,
                lines.length > 0);
    }

    private void assertStdoutIncludes(String text) {
        assertTrue("stdout does not include the expected text <"
                + text + ">:\n" + stdout, stdout.indexOf(text) != -1);
    }

    private void assertStderrIncludes(String text) {
        assertTrue("stderr does not include the expected text <"
                + text + ">:\n" + stderr, stderr.indexOf(text) != -1);
    }

    private void executeRobotTestGoal() throws IOException {
        String[] goals = new String[] { "-X", "-o", "-B",
                "robot:test" };
        executeMavenGoals(goals);
    }

    private void executeMavenGoals(String[] goals) throws IOException {
        project.createIn(projectRoot);
        List argsList = Arrays.asList(goals);
        String script = MavenInstallation.pathToMvn();
        Platform platform = configurePlatform();
        Execution mvn = platform.runScript(script, argsList,
                projectRoot);
        mvn.waitForCompletion();
        stdout = IO.readToString(mvn.getStdout());
        stderr = IO.readToString(mvn.getStderr());
        readOutputFilesFromStandardOutputAndErrorStreams();
    }

    private void readOutputFilesFromStandardOutputAndErrorStreams()
            throws IOException, FileNotFoundException {
        String[] lines = Grep
                .exact("Directing stdout and stderr to ")
                .from(stdout);
        if (lines.length < 1) {
            System.out.println("STANDARD OUTPUT WAS:\n" + stdout);
            return;
        }
        String line = lines[lines.length - 1];
        Matcher m = Pattern.compile(
                ".*Directing stdout and stderr to (.*) and (.*).*")
                .matcher(line);
        if (m.matches()) {
            String robotStdoutFile = m.group(1);
            robotStdout = IO.readToString(new FileInputStream(
                    robotStdoutFile));
            String robotStderrFile = m.group(2);
            robotStderr = IO.readToString(new FileInputStream(
                    robotStderrFile));
            System.out.println("robotStdout from " + robotStdoutFile
                    + ":\n" + robotStdout);
            System.out.println("robotStderr from " + robotStderrFile
                    + ":\n" + robotStderr);
        } else {
            System.out
                    .println("NO MATCH FOR 'Directing stdout and stderr to (.+?) and (.+?)'");
        }
    }

    private Platform configurePlatform() {
        Platform platform = Platform.resolve();
        platform.propagateEnvironmentVariable("PWD");
        platform.propagateEnvironmentVariable("PATH");
        platform.propagateEnvironmentVariable("HOME");
        platform.propagateEnvironmentVariable("USER");
        platform.propagateEnvironmentVariable("USER_HOME");
        platform.propagateEnvironmentVariable("JAVA_HOME");
        platform.propagateEnvironmentVariable("M2_HOME");
        platform.propagateEnvironmentVariable("MAVEN_OPTS");
        return platform;
    }
}
