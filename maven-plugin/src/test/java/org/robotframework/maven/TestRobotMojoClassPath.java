package org.robotframework.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.robotframework.maven.RobotMojo;
import org.robotframework.maven.mocks.FakeArtifact;


/**
 * @author Lasse Koskela
 */
public class TestRobotMojoClassPath extends TestCase {

    private static final File temp = new File(System
            .getProperty("java.io.tmpdir"));

    private RobotMojo mojo;

    private List classpath;

    protected void setUp() throws Exception {
        super.setUp();
        mojo = new RobotMojo();
        mojo.classesDirectory = new File(temp, "classesDirectory");
        mojo.testClassesDirectory = new File(temp,
                "testClassesDirectory");
        mojo.pluginArtifacts = new ArrayList();
        mojo.projectArtifacts = new HashSet();

        mojo.pluginArtifacts.add(new FakeArtifact("plugin-a.jar"));
        mojo.pluginArtifacts.add(new FakeArtifact("plugin-b.jar"));

        mojo.projectArtifacts.add(new FakeArtifact("project-a.jar"));
        mojo.projectArtifacts.add(new FakeArtifact("project-b.jar"));

        mojo.initialize();
        classpath = Arrays.asList(mojo.robotClassPath.split(System
                .getProperty("path.separator")));
    }

    public void testOutputDirectoryIsIncluded() throws Exception {
        assertClassPathIncludes(mojo.classesDirectory);
    }

    public void testTestOutputDirectoryIsIncluded() throws Exception {
        assertClassPathIncludes(mojo.testClassesDirectory);
    }

    public void testPluginArtifactsAreIncluded() throws Exception {
        for (Iterator i = mojo.pluginArtifacts.iterator(); i
                .hasNext();) {
            Artifact artifact = (Artifact) i.next();
            assertClassPathIncludes(artifact.getFile());
        }
    }

    public void testProjectArtifactsAreIncluded() throws Exception {
        for (Iterator i = mojo.projectArtifacts.iterator(); i
                .hasNext();) {
            Artifact artifact = (Artifact) i.next();
            assertClassPathIncludes(artifact.getFile());
        }
    }

    private void assertClassPathIncludes(File file) {
        assertTrue(classpath.contains(file.getAbsolutePath()));
    }
}
