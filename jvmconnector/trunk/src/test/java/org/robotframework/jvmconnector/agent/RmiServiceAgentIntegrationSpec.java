package org.robotframework.jvmconnector.agent;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class RmiServiceAgentIntegrationSpec extends Specification<Void> {
    private static String agentArguments = JarSpecUtil.jarDir + JarSpecUtil.pathSep + 
                                           JarSpecUtil.jarDir + JarSpecUtil.fileSep + "helper-keywords.jar";
    
    public class Any {
        public void addsJarsToClassPath() throws Exception {
            FakeInstrumentation instrumentation = new FakeInstrumentation();
            RmiServiceAgent.setClasspath(agentArguments, instrumentation);
            List<String> expectedJars = getExpectedJars("helper-keywords.jar");
            
            specify(expectedJars, instrumentation.appendedJars);
        }
        
        public void doesNothingWhenNoArgumentsAreGiven() {
            FakeInstrumentation instrumentation = new FakeInstrumentation();
            RmiServiceAgent.setClasspath(null, instrumentation);
            
            specify(instrumentation.appendedJars.isEmpty());
        }
    }
    
    private List<String> getExpectedJars(String... additionalJars) {
        Collection<File> jars = FileUtils.listFiles(new File(JarSpecUtil.jarDir), new String[] {"jar" }, true);
        List<String> expectedJars = new ArrayList<String>();
        for (File jar : jars) {
            expectedJars.add(jar.getName());
        }
        
        for (String jar : additionalJars) {
            expectedJars.add(jar);
        }
        return expectedJars;
    }
    
    
    private class FakeInstrumentation implements Instrumentation {
        List<String> appendedJars = new ArrayList<String>();
        
        public void appendToSystemClassLoaderSearch(JarFile jarfile) {
            appendedJars.add(JarSpecUtil.getSimpleName(jarfile));
        }
        
        public void addTransformer(ClassFileTransformer transformer) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public Class[] getAllLoadedClasses() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public Class[] getInitiatedClasses(ClassLoader loader) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public long getObjectSize(Object objectToSize) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public boolean isModifiableClass(Class<?> theClass) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public boolean isNativeMethodPrefixSupported() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public boolean isRedefineClassesSupported() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public boolean isRetransformClassesSupported() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException,
            UnmodifiableClassException {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public boolean removeTransformer(ClassFileTransformer transformer) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}
