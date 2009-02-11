/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.robotframework.jvmconnector.launch.jnlp.Jar;
import org.robotframework.jvmconnector.launch.jnlp.JarExtractor;
import org.robotframework.jvmconnector.util.Logger;
import org.robotframework.jvmconnector.xml.Document;
import org.robotframework.jvmconnector.xml.Document.MyElement;

public class WebstartLauncher {
    private final String javawsExecutable;
    private final String libraryResourceDir;

    public WebstartLauncher(String libraryResourceDir) {
        this(libraryResourceDir, "javaws");
    }

    public WebstartLauncher(String libraryResourceDir, String javawsExecutable) {
        this.libraryResourceDir = libraryResourceDir;
        this.javawsExecutable = javawsExecutable;
    }
    
    public void startWebstartApplicationAndRmiService(String rmiConfigFilePath, String jnlpUrl) throws Exception {
        Document modifiedJnlp = getModifiedJnlp(rmiConfigFilePath, jnlpUrl);
        File jnlpFileName = new File("modified.jnlp");
        modifiedJnlp.printTo(new PrintStream(new FileOutputStream(jnlpFileName)));
        String command = javawsExecutable + " " + jnlpFileName.getCanonicalPath();
        Logger.log("running following command '" + command + "'");
        Runtime.getRuntime().exec(command);
    }

    private Document getModifiedJnlp(String rmiConfigFilePath, String jnlpUrl) throws Exception {
        Document doc = createDocument(jnlpUrl);
        MyElement jnlp = doc.element("jnlp");
        jnlp.removeAttribute("href");
        MyElement appDesc = jnlp.element("application-desc");
        String mainClass = appDesc.getAttribute("main-class");
        if (mainClass == null || mainClass.length() == 0) {
            String codebase = jnlp.getAttribute("codebase");
            String firstJar = jnlp.element("resources").element("jar").getAttribute("href");
            String jarUrl = codebase + "/" + firstJar;
            Jar mainJar = new JarExtractor().createMainJar(jarUrl);
            mainClass = mainJar.getMainClass();
        }
        
        appDesc.setAttribute("main-class", RMILauncher.class.getName());
        appDesc.insertElement("argument").insertText(mainClass);
        appDesc.insertElement("argument").insertText(rmiConfigFilePath);
        
        addLibraryResources(jnlp);
        return doc;
    }

    private void addLibraryResources(MyElement jnlp) {
        List<String> jars = listJars();
        
        MyElement resourcesElement = jnlp.element("resources");
        for (String jar : jars) {
            resourcesElement.addElement("jar").setAttribute("href", jar);
        }
        
        resourcesElement.insertElement("jar").setAttribute("href", findFirstJarName());
    }

    private List<String> listJars() {
        File[] jars = new File(libraryResourceDir).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
        }});
        
        return toUrlFormat(jars); 
    }

    private List<String> toUrlFormat(File[] jars) {
        List<String> paths = new ArrayList<String>();
        for (File jar : jars) {
            try {
                paths.add("file://" + jar.getCanonicalPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return paths;
    }

    private String findFirstJarName() {
        List<String> jars = listJars();
        for (String jar : jars) {
            if (jar.toLowerCase().indexOf("jvmconnector") >= 0)
                return jar;
        }
        throw new IllegalStateException(libraryResourceDir + " doesn't contain jvmconnector jar.");
    }

    Document createDocument(String jnlpUrl) throws Exception {
        return new Document(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(jnlpUrl));
    }
    
    public static void main(String[] args) throws Exception {
        WebstartLauncher launcher = new WebstartLauncher("/var/www/jnlp") {
            @Override
            Document createDocument(String url) throws Exception {
                return new Document(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(url)));        
            }
        };

        launcher.getModifiedJnlp("foo", "src/test/resources/test-app/test-application.jnlp").printTo(System.out);
    }
}
