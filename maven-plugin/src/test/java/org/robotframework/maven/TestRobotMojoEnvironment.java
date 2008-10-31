package org.robotframework.maven;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.robotframework.maven.platform.Platform;


/**
 * @author Lasse Koskela
 */
public class TestRobotMojoEnvironment extends
        RobotMojoProcessLaunchTestCase {

    public void testPathIsPropagatedToEnvironmentByDefault()
            throws Exception {
        mojo.execute();
        assertEnvVariableWasPassedToRobot("PATH");
    }

    public void testClasspathIsPropagatedToEnvironmentByDefault()
            throws Exception {
        mojo.execute();
        String executedClasspath = "";
        for (Iterator iterator = executedEnvironment.iterator(); iterator
                .hasNext();) {
            String entry = (String) iterator.next();
            if (entry.startsWith("CLASSPATH=")) {
                executedClasspath = entry.substring("CLASSPATH="
                        .length());
            }
        }
        assertTrue(
                "Executed classpath should include project artifacts.",
                executedClasspath.indexOf(PROJECT_ARTIFACT_JAR) != -1);
    }

    private void assertEnvVariableWasPassedToRobot(String name)
            throws IOException {
        Platform platform = Platform.resolve();
        String value = platform.getEnvironmentVariable(name);
        assertEnvVariableWasPassedToRobot(name, value);
    }

    private void assertEnvVariableWasPassedToRobot(String name,
            String value) {
        assertTrue("Environment should've included " + name + "="
                + value + " but it didn't: " + executedEnvironment,
                executedEnvironment.contains(name + "=" + value));
    }

    public void testConfiguringEnvironmentVariablesExplicitly()
            throws Exception {
        mojo.robotEnvironmentVariables = new HashMap();
        mojo.robotEnvironmentVariables.put("FOO", "FOO_VALUE");
        mojo.robotEnvironmentVariables.put("BAR", "BAR_VALUE");
        mojo.execute();
        assertEnvVariableWasPassedToRobot("FOO", "FOO_VALUE");
        assertEnvVariableWasPassedToRobot("BAR", "BAR_VALUE");
    }
}
