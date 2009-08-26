package org.robotframework.jvmconnector.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import jdave.junit4.JDaveRunner;

import org.junit.runner.RunWith;
import org.robotframework.jdave.mock.MockSupportSpecification;

@RunWith(JDaveRunner.class)
public class JarFinderIntegrationSpec extends MockSupportSpecification<JarFinder> {
    private static String fileSep = System.getProperty("file.separator");
    private static String thisDir = new File(".").getAbsolutePath();
    private static String jarDir =  thisDir + fileSep + "src" + fileSep + "test" + fileSep + "resources" + fileSep + "test-lib";

    public class Any {
        public JarFinder create() throws IOException {
            return new JarFinder(jarDir);
        }

        public void findsJars() {
            final List<String> jarsFound = new ArrayList<String>();
            context.each(new JarFileAction() {
                public void doOnFile(JarFile jar) {
                    int lastFileSepIndex = jar.getName().lastIndexOf(fileSep);
                    String simpleName = jar.getName().substring(lastFileSepIndex + 1);
                    jarsFound.add(simpleName);
                }
            }); 
            
            specify(jarsFound, containsExactly("helper-keywords.jar", "jvmconnector.jar", "javalib-core.jar", "swinglibrary.jar"));
        }
    }
}
