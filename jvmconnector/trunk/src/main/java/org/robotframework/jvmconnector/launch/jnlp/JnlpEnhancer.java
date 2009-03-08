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


package org.robotframework.jvmconnector.launch.jnlp;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.robotframework.jvmconnector.xml.Document;
import org.robotframework.jvmconnector.xml.Document.MyElement;

public class JnlpEnhancer {
    private final String resourceDir;

    public JnlpEnhancer(String resourceDir) {
        this.resourceDir = resourceDir;
    }
    
    public String createRmiEnhancedJnlp(String rmiConfigFilePath, String jnlpUrl) throws Exception, FileNotFoundException {
        Document modifiedJnlp = createEnhancedJnlp(rmiConfigFilePath, jnlpUrl);
        String localName = getLocalName(jnlpUrl);
        modifiedJnlp.printTo(new PrintStream(new FileOutputStream(localName)));
        return localName;
    }

    private String getLocalName(String jnlpUrl) {
        try {
            URL url = new File(System.getProperty("java.io.tmpdir") + "/" + FilenameUtils.getName(jnlpUrl)).toURL();
            return url.getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createEnhancedJnlp(String rmiConfigFilePath, String jnlpUrl) throws Exception {
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
        
        appDesc.setAttribute("main-class", "RMILauncher");
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
        File[] jars = new File(resourceDir).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
        }});
        
        return toUrlFormat(jars); 
    }

    private List<String> toUrlFormat(File[] jars) {
        List<String> paths = new ArrayList<String>();
        for (File jar : jars) {
            paths.add(toURLFormat(jar));
        }
        return paths;
    }

    private String findFirstJarName() {
        List<String> jars = listJars();
        for (String jar : jars) {
            if (isJvmconnectorJar(jar))
                return jar;
        }
        throw new IllegalStateException(resourceDir + " doesn't contain jvmconnector jar.");
    }

    private boolean isJvmconnectorJar(String jar) {
        String jarBasename = getBasename(jar).toLowerCase();
        return jarBasename.indexOf("jvmconnector") >= 0;
    }

    private String getBasename(String jar) {
        return jar.substring(jar.lastIndexOf('/'));
    }

    Document createDocument(String jnlpUrl) throws Exception {
        return new Document(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(jnlpUrl));
    }

    private String toURLFormat(File file) {
        try {
            URL fileURL = file.toURL();
            return fileURL.toExternalForm();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
