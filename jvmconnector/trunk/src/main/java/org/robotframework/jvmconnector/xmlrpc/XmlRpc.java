package org.robotframework.jvmconnector.xmlrpc;

import java.net.URL;
import java.util.Collections;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class XmlRpc {
    public static void main(String[] args) throws Exception {
        startServer();
    }

    private static void startServer() throws Exception {
        WebServer webServer = new WebServer(9999);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      
        xmlRpcServer.setHandlerMapping(new XmlRpcHandlerMapping() {
            public XmlRpcHandler getHandler(String handlerName) throws XmlRpcNoSuchHandlerException, XmlRpcException {
                return new MyXmlRpcHandler();
            }
        });
      
        webServer.start();
        new Client().doSomething();
    }
}

class Client {
    public void doSomething() throws Exception {
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:9999/lehma"));
        config.setEnabledForExtensions(true);  
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);

        XmlRpcClient client = new XmlRpcClient();
      
        // use Commons HttpClient as transport
        client.setTransportFactory(
            new XmlRpcCommonsTransportFactory(client));
        // set configuration
        client.setConfig(config);

        // make the a regular call
        Object[] params = new Object[]
            { new Integer(2), new Integer(3) };
        Integer result = (Integer) client.execute("Calculator.add", params);
        System.out.println("2 + 3 = " + result);
      
        // make a call using dynamic proxy
        client.execute("lol", Collections.emptyList());
    }
}

class MyXmlRpcHandler implements XmlRpcHandler {
    public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
