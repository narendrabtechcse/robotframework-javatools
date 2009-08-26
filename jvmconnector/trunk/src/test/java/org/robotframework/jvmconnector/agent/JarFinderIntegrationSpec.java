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
    private static String tmpDir = System.getProperty("java.io.tmpdir");
    private static String fileSep = System.getProperty("file.separator");
    private static String jarDir = new File(".").getAbsolutePath() + fileSep + "src" + fileSep + "test" + fileSep + "resources" + fileSep + "test-lib";

    public class Any {
        public JarFinder create() throws IOException {
            return new JarFinder(jarDir);
        }

        public void findsJars() {
            final List<String> jarsFound = new ArrayList<String>();
            context.each(new JarFileAction() {
                public void doOnFile(JarFile jar) {
                    jarsFound.add(jar.getName());
                }
            }); 
            
            System.out.println(jarsFound);
            System.out.println(new File(".").getAbsolutePath());
//            specify(jars, containExactly(jarsFound));
        }
    }
    
    public void destroy() throws Exception {
//        FileUtils.deleteDirectory(new File(jarDir));
    }
}
