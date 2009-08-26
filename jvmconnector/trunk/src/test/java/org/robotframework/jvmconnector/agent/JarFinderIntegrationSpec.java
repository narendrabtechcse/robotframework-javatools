package org.robotframework.jvmconnector.agent;

import java.io.File;
import java.io.IOException;

import jdave.junit4.JDaveRunner;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.robotframework.jdave.mock.MockSupportSpecification;

@RunWith(JDaveRunner.class)
public class JarFinderIntegrationSpec extends MockSupportSpecification<JarFinder> {
    private static String tmpDir = System.getProperty("java.io.tmpdir");
    private static String fileSep = System.getProperty("file.separator");
    private static String jarDir = tmpDir + fileSep + "testjars"; 
    
    private static String[] jars = new String[] {
        jarDir + fileSep + "some.jar",
        jarDir + fileSep + "other.jar",
        jarDir + fileSep + "somedir" + fileSep + "some.jar"
    };
    
    public void create() throws Exception {
        for (String jar : jars) {
            FileUtils.touch(new File(jar));
        }
    }
    
    public class Any {
        public JarFinder create() throws IOException {
            for (String jar : jars) {
                FileUtils.touch(new File(jar));
            }
            return new JarFinder(jarDir);
        }

//        public void findsJars() {
//            final List<String> jarsFound = new ArrayList<String>();
//            context.each(new JarFileAction() {
//                public void doOnFile(JarFile jar) {
//                    jarsFound.add(jar.getName());
//                }
//            }); 
//            
//            
//            System.out.println(jarsFound);
//            specify(jars, containExactly(jarsFound));
//        }
    }
    
    public void destroy() throws Exception {
//        FileUtils.deleteDirectory(new File(jarDir));
    }
}
