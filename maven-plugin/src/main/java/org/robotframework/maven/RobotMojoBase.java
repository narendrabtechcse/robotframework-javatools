package org.robotframework.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;

/**
 * @author Lasse Koskela
 */
public abstract class RobotMojoBase extends AbstractMojo {

    /**
     * The directory containing generated test classes of the project being
     * tested.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    public File testClassesDirectory = new File("target/test-classes");

    /**
     * The directory containing generated classes of the project being tested.
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    public File classesDirectory = new File("target/classes");

    /**
     * List of of plugin artifacts.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    public List pluginArtifacts = new ArrayList();

    /**
     * Set of of project artifacts.
     * 
     * @parameter expression="${project.artifacts}"
     * @required
     * @readonly
     */
    public Set projectArtifacts = new HashSet();

    /**
     * The Robot script to execute tests with. Generally either "pybot" or
     * "jybot" (defaults to "pybot").
     * 
     * @parameter default-value="pybot"
     * @required
     */
    public String robotScript = "pybot";

    /**
     * The directory where Robot should execute tests from.
     * 
     * @parameter default-value="src/test/resources/robot-tests"
     * @required
     */
    public File robotTestDirectory = new File(
            "src/test/resources/robot-tests");

    /**
     * The command-line arguments to pass to the Robot script.
     * 
     * @parameter
     */
    public List robotArguments = new ArrayList();

    /**
     * The environment variables to pass to the Robot script.
     * 
     * @parameter
     */
    public Map robotEnvironmentVariables = new HashMap();

    /**
     * Whether or not the Robot output should be printed to the console.
     * 
     * @parameter default-value="false"
     */
    public boolean showConsoleOutput;
}
