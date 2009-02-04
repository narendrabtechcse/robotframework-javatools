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

import org.robotframework.javalib.beans.common.URLFileFactory;

public class JNLP {
    private final JNLPElement jnlpRootElement;
    private final URLFileFactory fileFactory;

    public JNLP(JNLPElement jnlpRootElement) {
        this(jnlpRootElement, new URLFileFactory(System.getProperty("java.io.tmpdir")));
    }
    
    public JNLP(JNLPElement jnlpRootElement, URLFileFactory fileFactory) {
        this.jnlpRootElement = jnlpRootElement;
        this.fileFactory = fileFactory;
    }

    public String getMainClass() {
        JNLPElement appDesc = jnlpRootElement.getFirstChildElement("application-desc");
        String mainClass = appDesc.getAttributeValue("main-class");
        if (mainClass == null) {
            String codeBase = jnlpRootElement.getAttributeValue("codebase");
            JNLPElement firstJarElement = jnlpRootElement.getFirstChildElement("resources").getFirstChildElement("jar");
//            fileFactory.createFileFromUrl(url)
        }
        return mainClass;
    }
}
