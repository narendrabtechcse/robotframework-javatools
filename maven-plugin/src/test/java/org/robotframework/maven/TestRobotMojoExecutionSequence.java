package org.robotframework.maven;

import org.robotframework.maven.RobotMojo;

import junit.framework.TestCase;

/**
 * @author Lasse Koskela
 */
public class TestRobotMojoExecutionSequence extends TestCase {

    protected boolean environmentWasInitialized;

    protected boolean robotWasLaunched;

    public void testClassPathIsInitializedUponExecute()
            throws Exception {
        new RobotMojo() {
            protected void initialize() {
                environmentWasInitialized = true;
            }

            protected void launchRobot() {
                assertTrue(
                        "execute() should initialize env before launching Robot",
                        environmentWasInitialized);
                robotWasLaunched = true;
            }
        }.execute();
        assertTrue("execute() should launch Robot", robotWasLaunched);
    }
}
