package org.robotframework.jvmconnector.keyword;


import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;
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
    public void stopJvm() {
        System.exit(0);
    }
}