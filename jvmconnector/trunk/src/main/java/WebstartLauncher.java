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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

public class WebstartLauncher {
    private final String javawsExecutable;

    public WebstartLauncher() {
        this("javaws");
    }

    public WebstartLauncher(String javawsExecutable) {
        this.javawsExecutable = javawsExecutable;
    }
    
    public void startWebstartApplicationAndRmiService(String rmiConfigFilePath, String jnlpUrl) {
        System.out.println("kukkuu");
    }
    
    public static void main(String[] args) throws Exception {
        Builder parser = new Builder();
//        String url = "http://robotframework-javatools.googlecode.com/svn/wiki/demo/jvmconnector-demo/test-application.jnlp";
        String url = "/var/www/jnlp/test-application.jnlp";
        Document doc = parser.build(url);
        Element rootElement = doc.getRootElement();
        Nodes nodes = rootElement.query("//application-desc/@main-class");
        System.out.println(nodes.get(0).getValue());
        Element appDesc = doc.getRootElement().getFirstChildElement("application-desc");
        String mainClass = appDesc.getAttributeValue("main-class");
//        System.out.println(mainClass);
        System.out.println(doc.toXML());
    }
}
