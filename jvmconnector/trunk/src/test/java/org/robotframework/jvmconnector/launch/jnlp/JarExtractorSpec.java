package org.robotframework.jvmconnector.launch.jnlp;

import java.io.File;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.robotframework.javalib.beans.common.URLFileFactory;

@RunWith(JDaveRunner.class)
public class JarExtractorSpec extends Specification<JarExtractor> {
    public class Any {
        private URLFileFactory fileFactory;
        private String jarFilePath = "/tmp/someJar.jar";
        
        public JarExtractor create() {
            fileFactory = mock(URLFileFactory.class);
            return new JarExtractor(fileFactory) {
                Jar createJar(final File jarFile) {
                    return new Jar() {
                        public String getMainClass() {
                            throw new UnsupportedOperationException("Not implemented.");
                        }

                        public String getPath() {
                            return jarFile.getPath();
                        }
                    };
                }
            };
        }
        
        public void extractsFirstJar() {
            final Element jnlpRootElement = mock(Element.class);
            final Element resourcesElement = mock(Element.class, "resources");
            final Element jarElement = mock(Element.class, "firstJar");
            final File jarFile = mock(File.class);
            
            checking(new Expectations() {{
                one(jnlpRootElement).getAttributeValue("codebase"); will(returnValue("http://somehost"));
                one(jnlpRootElement).getFirstChildElement("resources"); will(returnValue(resourcesElement));
                one(resourcesElement).getFirstChildElement("jar"); will(returnValue(jarElement));
                one(jarElement).getAttributeValue("href"); will(returnValue("someJar.jar"));
                
                one(fileFactory).createFileFromUrl("http://somehost/someJar.jar"); will(returnValue(jarFile));
                one(jarFile).getPath(); will(returnValue(jarFilePath));
                ignoring(jarFile);
            }});
            
            Jar firstJar = context.createMainJar(jnlpRootElement);
            specify(firstJar.getPath(), "/tmp/someJar.jar");
        }
    }
}
