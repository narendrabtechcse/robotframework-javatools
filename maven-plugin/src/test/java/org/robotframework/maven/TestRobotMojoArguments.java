package org.robotframework.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lasse Koskela
 */
public class TestRobotMojoArguments extends
        RobotMojoProcessLaunchTestCase {

    public void testPybotScriptIsCalledByDefault() throws Exception {
        mojo.execute();
        assertEquals("pybot", executedScript);
    }

    public void testRobotScriptCanBeConfigured() throws Exception {
        mojo.robotScript = "/path/to/jybot";
        mojo.execute();
        assertEquals(mojo.robotScript, executedScript);
    }

    public void testColorOutputIsDisabledByDefault() throws Exception {
        List arguments = new ArrayList();
        arguments.add("--monitorcolors");
        arguments.add("off");
        mojo.execute();
        assertArgumentWasPresent(arguments);
    }

    public void testOutputDirectoryIsSetByDefault() throws Exception {
        List arguments = new ArrayList();
        arguments.add("-d");
        arguments.add("target/robot");
        mojo.execute();
        assertArgumentWasPresent(arguments);
    }

    public void testArgumentsCanBeConfigured() throws Exception {
        List arguments = new ArrayList();
        arguments.add("-c");
        arguments.add("criticalTag");
        arguments.add("-e");
        arguments.add("excludedTag");
        mojo.robotArguments = arguments;
        mojo.execute();
        assertArgumentWasPresent(arguments);
    }

    public void testTestsAreExecutedFromSrcTestResourcesRobotTestsByDefault()
            throws Exception {
        mojo.execute();
        assertArgumentWasPresent("src/test/resources/robot-tests");
        mojo.robotTestDirectory = new File("test/robot-tests");
        mojo.execute();
        assertArgumentWasNotPresent("src/test/resources/robot-tests");
        assertArgumentWasPresent("test/robot-tests");
    }
    
    public void testConsoleOutputCanBeEnabled() throws Exception {
        mojo.showConsoleOutput = true;
        mojo.execute();
    }
}
