package org.robotframework.jvmconnector.xmlrpc;

import org.robotframework.jvmconnector.mocks.MockJavaLibrary;

public class XmlRpcStarter {
    public void startXmlRpcServer() throws Exception {
        RobotXmlRpcServer server = new RobotXmlRpcServer(new MockJavaLibrary());
        server.startServer();
    }    
}
