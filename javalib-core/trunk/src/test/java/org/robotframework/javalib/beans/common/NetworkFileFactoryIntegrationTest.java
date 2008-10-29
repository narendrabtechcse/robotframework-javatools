package org.robotframework.javalib.beans.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

public class NetworkFileFactoryIntegrationTest {
    private static Server server;
    private static String resourceBase = "./src/test/resources";
    
    private String localDirectoryPath = System.getProperty("java.io.tmpdir");
    private String fileSeparator = System.getProperty("file.separator");
    private String localFilePath = localDirectoryPath + "/network_file.txt";
    private String url = "http://localhost:8080/network_file.txt";
    
    @BeforeClass
    public static void startFileServer() throws Exception {
        server = new Server(8080);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(resourceBase);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
        server.start();
    }

    @Before
    public void deleteTemporaryResources() {
        new File(localFilePath).delete();
    }
    
    @AfterClass
    public static void stopServer() throws Exception {
        try {
            server.destroy(); // calling stop() would make maven hang
        } catch (Exception e) {
            // Ignored intentionally
        }
    }

    @Test
    public void retrievesFileFromURL() throws Exception {
        File localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        assertFileEqualsToUrl(localFile);
    }

    @Test
    public void doesntRetrieveFileWhenLocalCopyExistsAndURLHasntChanged() throws Exception {
        File localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        long lastModified = localFile.lastModified();
        localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        
        assertEquals(lastModified, localFile.lastModified());
    }
    
    @Test
    public void doesRetrieveFileWhenURLHasChanged() throws Exception {
        File localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        long lastModified = localFile.lastModified();
        updateURLContent();
        localFile = new NetworkFileFactory(localDirectoryPath).createFileFromUrl(url);
        
        assertTrue(lastModified < localFile.lastModified());
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
    
    private void updateURLContent() throws IOException {
        FileUtils.touch(new File(resourceBase + fileSeparator + "network_file.txt"));
    }
}
