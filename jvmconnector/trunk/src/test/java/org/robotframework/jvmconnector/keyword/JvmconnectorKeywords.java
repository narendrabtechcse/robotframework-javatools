package org.robotframework.jvmconnector.keyword;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;
import org.robotframework.jvmconnector.launch.jnlp.JnlpEnhancer;
import org.robotframework.jvmconnector.mocks.MyApplication;


@RobotKeywords
public class JvmconnectorKeywords extends AnnotationLibrary {
    public JvmconnectorKeywords() {
        super("org/robotframework/jvmconnector/keyword/**/*.class");
    }
    
    @RobotKeyword
    public boolean applicationIsRunning() {
        return MyApplication.isRunning;
    }
    
    @RobotKeyword
    public void stopApplication() {
        MyApplication.isRunning = false;
    }
    
    @RobotKeyword
    public String[] getArguments() {
        return MyApplication.args;
    }
    
    @RobotKeyword
    public void stopJvm() {
        System.exit(0);
    }
    
    @RobotKeyword
    public String getEnhancedJnlp(String libraryResourceDir, String rmiConfigFilePath, String jnlpUrl) throws Exception {
        JnlpEnhancer jnlpRunner = new JnlpEnhancer(libraryResourceDir);
        String pathToJnlp = jnlpRunner.createRmiEnhancedJnlp(rmiConfigFilePath, jnlpUrl);
        return FileUtils.readFileToString(new File(pathToJnlp), "UTF-8");
    }
    
    @RobotKeyword
    public void startAnotherInstance(String[] args) {
        MyApplication.startAnotherInstance(args);
    }
}