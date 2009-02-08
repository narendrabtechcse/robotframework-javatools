package org.robotframework.jvmconnector.launch.jnlp;

import java.io.StringReader;

import jdave.Specification;
import nu.xom.Builder;
import nu.xom.Document;

public class JNLPIntegrationSpec extends Specification<JNLP> {
    public class WritingItself {
        private String jnlp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<jnlp spec=\"1.0+\"" +
            "      codebase=\"http://localhost/jnlp/\" " +
            "      href=\"test-application.jnlp\">" +
            "   <information>" +
            "      <title>JvmConnector Demo</title>" +
            "      <vendor>http://www.robotframework.org</vendor>" +
            "      <description>JvmConnector Demo</description>" +
            "      <homepage href=\"http://code.google.com/p/robotframework-javatools/wiki/JvmConnector\"/>" +
            "      <offline-allowed/>" +
            "   </information>" +
            "    <security><all-permissions/></security>" +
            "    <resources>" +
            "        <jar href=\"test-application.jar\"/>" +
            "        <jar href=\"jvmconnector-0.5.jar\"/>" +
            "        <jar href=\"swinglibrary-0.5-jre1.4.jar\"/>" +
            "        <j2se version=\"1.4+\" href=\"http://java.sun.com/products/autodl/j2se\"/>" +
            "    </resources>" +
            "    <application-desc main-class=\"org.robotframework.swing.testapp.TestApplication\"/>" +
            "</jnlp>";
        
        private Document doc;
        
        public JNLP create() throws Exception {
            Builder parser = new Builder();
            doc = parser.build(new StringReader(jnlp));
            Element rootElement = new Element(doc.getRootElement());
            return new JNLP(rootElement);
        }
        
        public void foo() {
            specify(context.getMainClass(), "org.robotframework.swing.testapp.TestApplication");
        }
    }
}
