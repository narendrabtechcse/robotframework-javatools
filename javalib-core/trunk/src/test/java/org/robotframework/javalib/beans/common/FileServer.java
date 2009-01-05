package org.robotframework.javalib.beans.common;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

public class FileServer {
    public static final String resourceBase = "./src/test/resources";
    private static Server server;
    
    public static void start() throws Exception {
        if (server != null && server.isStarted())
            return;
        server = new Server(19080);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(resourceBase);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
        server.start();
    }
    
    public static void stop() throws Exception {
        try {
            server.destroy(); // calling stop() would make maven hang
        } catch (Exception e) {
            // Ignored intentionally
        }
    }
    
    public static void main(String[] args) throws Exception {
        start();
    }
}
