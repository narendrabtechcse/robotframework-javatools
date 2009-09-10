package org.robotframework.jvmconnector.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.robotframework.javalib.library.RobotJavaLibrary;

public class RobotXmlRpcServer {
         
    private final RobotJavaLibrary library;
    private final int port;
    
    public RobotXmlRpcServer(RobotJavaLibrary library, int port) {
        this.library = library;
        this.port = port;
    }
    
    public void startServer() throws Exception {
        WebServer webServer = new WebServer(port);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        xmlRpcServer.setHandlerMapping(new XmlRpcHandlerMapping() {
            @SuppressWarnings("serial")
            private final Map<String, XmlRpcHandler> handlers = new HashMap<String, XmlRpcHandler>() {{ 
                put("get_keyword_names", new GetKeywordNamesHandler(library)); 
                put("run_keyword", new RunKeywordHandler(library));
            }};
            public XmlRpcHandler getHandler(String handlerName) throws XmlRpcNoSuchHandlerException, XmlRpcException {
                return handlers.get(handlerName);
            }
        });
      
        webServer.start();
        System.out.println("XMLRPC Server up and running...");
    }
}

class GetKeywordNamesHandler implements XmlRpcHandler {
    private final RobotJavaLibrary library;
    public GetKeywordNamesHandler(RobotJavaLibrary library) {
        this.library = library;
    }

    public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {
        return library.getKeywordNames();
    }
}

class RunKeywordHandler implements XmlRpcHandler {
    private final RobotJavaLibrary library;

    public RunKeywordHandler(RobotJavaLibrary library) {
        this.library = library;
    }

    @SuppressWarnings("serial")
    public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {
        try {
            int count = pRequest.getParameterCount();
            Object[] keywordArguments = new Object[count];
            for (int i=0; i < count; i++) {
                Object param = pRequest.getParameter(i);
                keywordArguments[i] = param;
            }
            String methodName = (String)pRequest.getParameter(0);            
            final Object rslt = library.runKeyword(methodName, (Object[])pRequest.getParameter(1) );
            return new HashMap<String, String>() {{
                put("status", "PASS");
                put("return", ""+rslt);
            }};
        } catch (final Throwable t) {
            System.out.println(""+t);
            final StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement elem :t.getStackTrace())
                stackTrace.append(elem).append("\n");
            return new HashMap<String, String>() {{
                put("status", "FAIL");
                put("error", t.getMessage());
                put("traceback", stackTrace.toString());                
            }};
        }
    }
}