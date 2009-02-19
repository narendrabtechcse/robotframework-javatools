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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.robotframework.jvmconnector.launch.jnlp.Jar;
import org.robotframework.jvmconnector.launch.jnlp.JarExtractor;
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
        String pathToJnlp = createRmiEnhancedJnlp(rmiConfigFilePath, jnlpUrl);
        launchRmiEnhancedJnlp(pathToJnlp);
    }

    private Process launchRmiEnhancedJnlp(String jnlpFile) throws IOException {
        return Runtime.getRuntime().exec(javawsExecutable + " \"" + jnlpFile + "\"");
    }

    private String createRmiEnhancedJnlp(String rmiConfigFilePath, String jnlpUrl) throws Exception, FileNotFoundException {
        Document modifiedJnlp = createEnhancedJnlp(rmiConfigFilePath, jnlpUrl);
        String localName = getLocalName(jnlpUrl);
        modifiedJnlp.printTo(new PrintStream(new FileOutputStream(localName)));
        return localName;
    }

    private String getLocalName(String jnlpUrl) {
        try {
            return new File(System.getProperty("java.io.tmpdir") + "/" + FilenameUtils.getName(jnlpUrl)).getCanonicalPath();
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
            paths.add("file:///" + jar.getAbsolutePath().replace('\\', '/'));
        }
        return paths;
    }

    private String findFirstJarName() {
        List<String> jars = listJars();
        for (String jar : jars) {
            if (isJvmconnectorJar(jar))
                return jar;
        }
        throw new IllegalStateException(libraryResourceDir + " doesn't contain jvmconnector jar.");
    }

    private boolean isJvmconnectorJar(String jar) {
        String jarBasename = getBasename(jar).toLowerCase();
        return jarBasename.indexOf("jvmconnector") >= 0;
    }

    private String getBasename(String jar) {
        return jar.substring(jar.lastIndexOf('/'));
    }

    Document createDocument(String jnlpUrl) throws Exception {
        setProxyIfNecessary();
        try {
            return new Document(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(jnlpUrl));
        } finally {
            clearProxy();
        }
    }
    
    private void setProxyIfNecessary() {
        if (proxyDefinedInEnvironment()) {
            setProxy();
        }
    }

    private boolean proxyDefinedInEnvironment() {
        return getProxyFromEnv() != null;
    }
    
    private void setProxy() {
        URL url = createURL(getProxyFromEnv());
        System.setProperty("http.proxyHost", url.getHost());
        System.setProperty("http.proxyPort", Integer.toString(url.getPort()));
    }
    
    private URL createURL(String proxy) {
        try {
            return new URL(proxy);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProxyFromEnv() {
        String proxy = System.getenv("HTTP_PROXY");
        if (proxy == null) {
            proxy = System.getenv("http_proxy");
        }
        
        return proxy;
    }
    
    private void clearProxy() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
    }
}
