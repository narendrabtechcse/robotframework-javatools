package org.robotframework.maven;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.robotframework.maven.RobotMojo;
import org.robotframework.maven.RobotMojoBase;
import org.robotframework.maven.mocks.FakeArtifact;
import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;
import org.robotframework.maven.platform.Unix;

import junit.framework.TestCase;


/**
 * @author Lasse Koskela
 */
public abstract class RobotMojoProcessLaunchTestCase extends TestCase {

    protected static final String PLUGIN_ARTIFACT_JAR = "plugin.jar";

    protected static final String PROJECT_ARTIFACT_JAR = "project.jar";

    private static final File temp = new File(System
            .getProperty("java.io.tmpdir"));

    protected RobotMojoBase mojo;

    protected String executedScript;

    protected List executedArguments;

    protected List executedEnvironment;

    protected void setUp() throws Exception {
        super.setUp();
        mojo = new RobotMojo() {
            protected Platform getPlatform() {
                return new Unix() {
                    public Execution runScript(String file, List args)
                            throws IOException {
                        executedScript = file;
                        executedArguments = args;
                        executedEnvironment = Arrays
                                .asList(getEnvironmentVariables());
                        Execution execution = new Execution(
                                Collections.singletonList("pwd"));
                        execution.run();
                        return execution;
                    }
                };
            }

            protected List convertIntoArgumentFile(List args) {
                // don't convert arguments into the usual "--argumentfile"
                // in the name of simpler tests
                return args;
            }
        };
        mojo.classesDirectory = new File(temp, "classesDir");
        mojo.testClassesDirectory = new File(temp, "testClassesDir");
        mojo.pluginArtifacts = Collections
                .singletonList(new FakeArtifact(PLUGIN_ARTIFACT_JAR));
        mojo.projectArtifacts = new HashSet(
                Collections.singletonList(new FakeArtifact(
                        PROJECT_ARTIFACT_JAR)));
    }

    protected void assertEnvironmentVariableWasPropagated(String name) {
        String value = System.getenv(name);
        String representation = name + "=" + value;
        assertTrue(
                "Environment should've included " + representation,
                executedEnvironment.contains(representation));
    }

    protected void assertArgumentWasPresent(List arguments) {
        assertContainsInOrder(executedArguments, arguments);
    }

    private void assertContainsInOrder(List haystack, List needle) {
        for (int i = 0; i <= haystack.size() - needle.size(); i++) {
            List subset = haystack.subList(i, i + needle.size());
            if (needle.equals(subset)) {
                return;
            }
        }
        fail("Expected sequence " + needle + " not found in "
                + haystack);
    }

    protected void assertArgumentWasNotPresent(String argument) {
        assertFalse("Argument " + argument + " was present in "
                + executedArguments + " against our expectations.",
                executedArguments.contains(argument));
    }

    protected void assertArgumentWasPresent(String argument) {
        assertTrue("Argument " + argument + " was not present in "
                + executedArguments, executedArguments
                .contains(argument));
    }
}
