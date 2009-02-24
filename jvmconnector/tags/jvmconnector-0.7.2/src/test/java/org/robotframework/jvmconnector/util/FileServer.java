package org.robotframework.jvmconnector.util;

import java.io.File;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

public class FileServer {
    public static final int PORT = 14563;
    public static final String URL_BASE = "http://localhost:" + PORT;
    private static Server server;

    public static void start(String resourceBase) throws Exception {
        if (server != null && server.isStarted())
            return;
        server = new Server(PORT);
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
        String resourceBase = new File("./src/test/resources").getCanonicalPath();
        System.out.println(resourceBase);
        start(resourceBase);
    }
}
