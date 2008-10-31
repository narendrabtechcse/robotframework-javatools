package org.robotframework.maven.log;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class MavenLog {

    private static Log logInstance = new SystemStreamLog();

    public static void setLog(Log log) {
        logInstance = log;
    }

    public static void debug(CharSequence content) {
        logInstance.debug(content);
    }

    public static void debug(Throwable error) {
        logInstance.debug(error);
    }

    public static void debug(CharSequence content, Throwable error) {
        logInstance.debug(content, error);
    }

    public static void error(CharSequence content) {
        logInstance.error(content);
    }

    public static void error(Throwable error) {
        logInstance.error(error);
    }

    public static void error(CharSequence content, Throwable error) {
        logInstance.error(content, error);
    }

    public static void info(CharSequence content) {
        logInstance.info(content);
    }

    public static void info(Throwable error) {
        logInstance.info(error);
    }

    public static void info(CharSequence content, Throwable error) {
        logInstance.info(content, error);
    }

    public static void warn(CharSequence content) {
        logInstance.warn(content);
    }

    public static void warn(Throwable error) {
        logInstance.warn(error);
    }

    public static void warn(CharSequence content, Throwable error) {
        logInstance.warn(content, error);
    }

    public static boolean isDebugEnabled() {
        return logInstance.isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return logInstance.isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        return logInstance.isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return logInstance.isErrorEnabled();
    }

    public static Log getLog() {
        return logInstance;
    }
}
