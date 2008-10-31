package org.robotframework.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.robotframework.maven.log.MavenLog;
import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.util.ClasspathBuilder;
import org.robotframework.maven.util.IO;


/**
 * @author Lasse Koskela
 * @version $Id$
 * @goal test
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class RobotMojo extends RobotMojoBase {

    protected String robotClassPath;

    public void execute() throws MojoExecutionException,
            MojoFailureException {
        initialize();
        try {
            launchRobot();
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to launch Robot.", e);
        }
    }

    private String createClassPath() {
        ClasspathBuilder cp = new ClasspathBuilder(true);
        cp.add(classesDirectory);
        cp.add(testClassesDirectory);
        cp.addContainerOf(getClass());
        cp.add(pluginArtifacts);
        cp.add(projectArtifacts);
        return cp.toString();
    }

    protected void initialize() {
        MavenLog.setLog(getLog());
        robotClassPath = createClassPath();
        getLog().debug(
                "initialized Robot class path to " + robotClassPath);
    }

    protected void launchRobot() throws IOException,
            MojoFailureException {
        List args = createArguments();
        Platform platform = getPlatform();
        configureEnvironment(platform);
        logRobotExecution(args, platform);
        args = convertIntoArgumentFile(args);
        Execution execution = platform.runScript(robotScript, args);
        checkRobotExitCode(execution);
    }

    protected List convertIntoArgumentFile(List args) {
        try {
            File argumentFile = createArgumentFile(args);
            List argFileArguments = new ArrayList();
            argFileArguments.add("--argumentfile");
            argFileArguments.add(argumentFile);
            return argFileArguments;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createArgumentFile(List args) throws IOException {
        File argFile = File.createTempFile("robot-argument", ".txt");
        argFile.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(argFile));
        for (Iterator i = args.iterator(); i.hasNext();) {
            out.println(String.valueOf(i.next()));
        }
        out.close();
        return argFile;
    }

    private void checkRobotExitCode(Execution execution)
            throws MojoFailureException {
        int exitCode = execution.getExitCode();
        String exitCodeMsg = "Robot returned with exit code "
                + exitCode;
        getLog().debug(exitCodeMsg);
        if (exitCode != 0) {
            try {
                IO.copy(execution.getStderr(), System.err);
                IO.copy(execution.getStdout(), System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new MojoFailureException(exitCodeMsg);
        }
    }

    private void logRobotExecution(List args, Platform environment) {
        StringBuffer msg = new StringBuffer(100);
        msg.append("Launching Robot with arguments " + args);
        if (getLog().isDebugEnabled()) {
            msg.append(" in environment " + environment);
            getLog().debug(msg);
        } else {
            getLog().info(msg);
        }
    }

    private List createArguments() {
        List command = new ArrayList();
        command.addAll(robotArguments);
        disableMonitorColors(command);
        configureOutputDirectory(command);
        command.add(robotTestDirectory.getPath());
        return command;
    }

    private void disableMonitorColors(List command) {
        command.add("--monitorcolors");
        command.add("off");
    }

    private void configureOutputDirectory(List command) {
        command.add("-d");
        command.add("target/robot");
    }

    private void configureEnvironment(Platform env) {
        env.propagateEnvironmentVariable("PATH");
        env.setEnvironmentVariable("CLASSPATH", robotClassPath);
        Iterator i = robotEnvironmentVariables.keySet().iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            String value = (String) robotEnvironmentVariables
                    .get(name);
            env.setEnvironmentVariable(name, value);
        }
    }

    protected Platform getPlatform() {
        return Platform.resolve();
    }
}
