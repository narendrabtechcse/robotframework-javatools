/* Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
