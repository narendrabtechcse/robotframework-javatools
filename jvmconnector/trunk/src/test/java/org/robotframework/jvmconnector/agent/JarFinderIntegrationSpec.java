package org.robotframework.jvmconnector.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import jdave.junit4.JDaveRunner;

import org.junit.runner.RunWith;
import org.robotframework.jdave.mock.MockSupportSpecification;

@RunWith(JDaveRunner.class)
public class JarFinderIntegrationSpec extends MockSupportSpecification<String> {
    private static String fileSep = System.getProperty("file.separator");
    private static String thisDir = new File(".").getAbsolutePath();
    private static String jarDir =  thisDir + fileSep + "src" + fileSep + "test" + fileSep + "resources" + fileSep + "test-lib";

    public class HandlingDirectory {
        public String create() {
            return jarDir;
        }

        public void findsJars() {
            List<String> expectedJars = findJars(context);
            specify(expectedJars, containsExactly("helper-keywords.jar", "jvmconnector.jar", "javalib-core.jar", "swinglibrary.jar"));
        }
    }
    
    public class HandlingFile {
        public String create() {
            return jarDir + fileSep + "helper-keywords.jar";
        }

        public void findsJars() {
            List<String> expectedJars = findJars(context); 
            specify(expectedJars, containsExactly("helper-keywords.jar"));
        }
    }

    private List<String> findJars(String file) {
        final List<String> jarsFound = new ArrayList<String>();
        JarFinder jarFinder = new JarFinder(file);
        jarFinder.each(new JarFileAction() {
            public void doOnFile(JarFile jar) {
                int lastFileSepIndex = jar.getName().lastIndexOf(fileSep);
                String simpleName = jar.getName().substring(lastFileSepIndex + 1);
                jarsFound.add(simpleName);
            }
        });
        return jarsFound;
    }
}
