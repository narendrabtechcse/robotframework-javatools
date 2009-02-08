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

import java.io.File;

import org.robotframework.javalib.beans.common.URLFileFactory;

public class JarExtractor {
    private final URLFileFactory fileFactory;

    public JarExtractor() {
        this(new URLFileFactory(System.getProperty("java.io.tmpdir")));
    }
    
    public JarExtractor(URLFileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public Jar createMainJar(Element jnlpRoot) {
        return createJar(getMainJarFile(jnlpRoot));
    }

    Jar createJar(File jarFile) {
        return new JarImpl(jarFile);
    }
    
    private File getMainJarFile(Element jnlpRoot) {
        return fileFactory.createFileFromUrl(getMainJarUrl(jnlpRoot));
    }
    
    private String getMainJarUrl(Element jnlpRoot) {
        return getCodeBase(jnlpRoot) + "/" + getMainJarFileName(jnlpRoot);
    }
    
    private String getCodeBase(Element jnlpRoot) {
        return jnlpRoot.getAttributeValue("codebase");
    }
    
    private String getMainJarFileName(Element jnlpRoot) {
        Element jarElement = jnlpRoot.getFirstChildElement("resources").getFirstChildElement("jar");
        return jarElement.getAttributeValue("href");
    }
}
