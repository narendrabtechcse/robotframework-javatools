import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

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

public class JNLPLauncher {
    private final String javawsExecutable;

    public JNLPLauncher() {
        this("javaws");
    }

    public JNLPLauncher(String javawsExecutable) {
        this.javawsExecutable = javawsExecutable;
    }
    
    public void startJnlpAndRMIService(String rmiConfigFilePath, String jnlpUrl) {
    }
    
    public static void main(String[] args) throws Exception {
        Builder parser = new Builder();
        String url = "http://robotframework-javatools.googlecode.com/svn/wiki/demo/jvmconnector-demo/test-application.jnlp";
        Document doc = parser.build(url);
        Element appDesc = doc.getRootElement().getFirstChildElement("application-desc");
        String mainClass = appDesc.getAttributeValue("main-class");
//        System.out.println(mainClass);
        System.out.println(doc.toXML());
    }
}
