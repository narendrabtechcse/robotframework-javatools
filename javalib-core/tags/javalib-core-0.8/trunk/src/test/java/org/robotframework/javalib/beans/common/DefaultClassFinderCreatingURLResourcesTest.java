package org.robotframework.javalib.beans.common;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.junit.Before;
import org.laughingpanda.beaninject.Inject;


public class DefaultClassFinderCreatingURLResourcesTest extends MockObjectTestCase {
    private DefaultClassFinder defaultClassFinder;
    private File pathToJar = new File("./src/test/resources/test.jar");
    private JarFile expectedJarFile;

    @Before
    protected void setUp() throws Exception {
        defaultClassFinder = new DefaultClassFinder() {
            JarFile createJarFile(File fileFromUrl) throws IOException {
                expectedJarFile = new JarFile(pathToJar);
                return expectedJarFile;
            }
        };
    }

    public void testCreatesJarFileFromURL() throws Exception {
        Mock fileFactory = injectMockFileFactoryToClassFinder();
        String url = "http://somedomain/someresource.jar";
        fileFactory.expects(once()).method("createFileFromUrl")
            .with(eq(url))
            .will(returnValue(pathToJar));
        
        JarFile actualJarFile = defaultClassFinder.getJarFile(url);
        assertEquals(expectedJarFile, actualJarFile);
    }

    private Mock injectMockFileFactoryToClassFinder() {
        Mock fileFactory = mock(URLFileFactory.class, new Class[] { String.class }, new Object[] { "/tmp" });
        Inject.field("urlFileFactory").of(defaultClassFinder).with(fileFactory.proxy());
        return fileFactory;
    }
}
