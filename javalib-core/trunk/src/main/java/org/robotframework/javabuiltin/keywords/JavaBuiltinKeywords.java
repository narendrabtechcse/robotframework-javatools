/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robotframework.javabuiltin.keywords;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class JavaBuiltinKeywords {
    @RobotKeyword("Sets system property.\n"
        + "See http://java.sun.com/javase/6/docs/api/java/lang/System.html#setProperty(java.lang.String,%20java.lang.String) for details\n\n"
        + "Example:\n"
        + "| Set System Property | _http.proxyHost_ | _myproxy.com_ | # sets the proxy host |\n"
        + "| Set System Property | _http.proxyPort_ | _8080_ | # sets the proxy port \n")
    public void setSystemProperty(String key, String value) {
        System.setProperty(key, value);
    }
}
