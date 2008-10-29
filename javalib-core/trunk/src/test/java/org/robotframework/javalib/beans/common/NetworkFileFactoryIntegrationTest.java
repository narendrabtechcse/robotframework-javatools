package org.robotframework.javalib.beans.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

public class NetworkFileFactoryIntegrationTest extends TestCase {
    private Server server;
    private String localDirectoryPath = System.getProperty("java.io.tmpdir");
    private String localFilePath = localDirectoryPath + "/network_file.txt";
    private String url = "http://localhost:8080/network_file.txt";

    protected void setUp() throws Exception {
        startFileServer();
        deleteTemporaryResources();
    }

    protected void tearDown() throws Exception {
        server.stop();
    }
    
    public void testRetrievesFileFromURL() throws Exception {
        File localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        
        assertFileEqualsToUrl(localFile);
    }
    
    private void startFileServer() throws Exception {
        server = new Server(8080);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("./src/test/resources");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
        server.start();
    }

    private void deleteTemporaryResources() {
        new File(localFilePath).delete();
    }
    
    private void assertFileEqualsToUrl(File file) throws Exception {
        assertStreamsEqual(new FileInputStream(file), new URL(url).openStream());
    }

    private void assertStreamsEqual(InputStream expectedStream, InputStream actualStream) throws IOException {
        try {
            assertTrue(IOUtils.contentEquals(expectedStream, actualStream));
        } finally {
            actualStream.close();
            expectedStream.close();
        }
    }
}
