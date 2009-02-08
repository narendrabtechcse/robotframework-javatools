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

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.robotframework.jvmconnector.xml.Document;
import org.robotframework.jvmconnector.xml.Document.MyElement;

public class WebstartLauncher {
    private final String javawsExecutable;

    public WebstartLauncher() {
        this("javaws");
    }

    public WebstartLauncher(String javawsExecutable) {
        this.javawsExecutable = javawsExecutable;
    }
    
    public void startWebstartApplicationAndRmiService(String rmiConfigFilePath, String jnlpUrl) throws Exception {
        
    }
    
    public static void main(String[] args) throws Exception {
        String url = "./src/test/resources/test-app/test-application.jnlp";
        Document doc = new Document(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(url)));
        MyElement appDesc = doc.element("application-desc");
//        String mainClass = appDesc.getAttribute("main-class");
//        if (mainClass == null) {
//            mainClass = doc.getA
//        }
        
        appDesc.insertElement("arg").insertText("foo");
        appDesc.insertElement("arg").insertText("bar");
        
        doc.printTo(System.out);
    }
}
