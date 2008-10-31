package org.robotframework.maven.testutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.robotframework.maven.util.IO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class MavenProjectTemplate {

    private Document pom;

    public File srcTestResources;

    public String robotTestDirectory;

    public String robotScript;

    public final List robotArguments;

    public final Map robotEnvironmentVariables;

    public MavenProjectTemplate(String pathToPomXml) {
        this(new File(pathToPomXml));
    }

    public MavenProjectTemplate(File pomXml) {
        pom = XML.parse(pomXml);
        robotArguments = new ArrayList();
        robotEnvironmentVariables = new HashMap();
    }

    private void updatePom() {
        Element plugins = (Element) pom.getElementsByTagName(
                "plugins").item(0);
        Element plugin = pom.createElement("plugin");

        plugin.appendChild(createSimpleElement("groupId", "robot"));
        plugin.appendChild(createSimpleElement("artifactId",
                "robot-maven-plugin"));

        Element configuration = pom.createElement("configuration");
        if (robotTestDirectory != null) {
            configuration.appendChild(createSimpleElement(
                    "robotTestDirectory", robotTestDirectory));
        }
        if (robotScript != null) {
            configuration.appendChild(createSimpleElement(
                    "robotScript", robotScript));
        }

        Element robotArguments = pom.createElement("robotArguments");
        for (Iterator i = this.robotArguments.iterator(); i.hasNext();) {
            robotArguments.appendChild(createSimpleElement("param",
                    String.valueOf(i.next())));
        }
        configuration.appendChild(robotArguments);

        Element robotEnvironmentVariables = pom
                .createElement("robotEnvironmentVariables");
        for (Iterator i = this.robotEnvironmentVariables.keySet()
                .iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = (String) this.robotEnvironmentVariables
                    .get(name);
            robotEnvironmentVariables
                    .appendChild(createSimpleElement(name, value));
        }
        configuration.appendChild(robotEnvironmentVariables);

        plugin.appendChild(configuration);
        plugins.appendChild(plugin);
    }

    private Element createSimpleElement(String tagName, String content) {
        Element element = pom.createElement(tagName);
        element.appendChild(pom.createTextNode(content));
        return element;
    }

    public void createIn(File directory) {
        try {
            createStandardDirectoryLayoutIn(directory);
            createRobotTestsDirectory();
            updatePom();
            XML.serialize(pom, new File(directory, "pom.xml"));
            System.out.println("Maven project created at "
                    + directory.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRobotTestsDirectory() throws IOException,
            FileNotFoundException {
        File robotTests = makeDir(srcTestResources, "robot-tests");
        InputStream robotTest = getClass().getResourceAsStream(
                "/robot-tests/interpreter_test.html");
        IO.copy(robotTest, new FileOutputStream(new File(robotTests,
                "test.html")));
    }

    private void createStandardDirectoryLayoutIn(File directory) {
        makeDir(directory, "src/main/java");
        makeDir(directory, "src/main/resources");
        makeDir(directory, "src/test/java");
        srcTestResources = makeDir(directory, "src/test/resources");
    }

    private File makeDir(File parent, String path) {
        File dir = new File(parent, path);
        dir.mkdirs();
        return dir;
    }
}
