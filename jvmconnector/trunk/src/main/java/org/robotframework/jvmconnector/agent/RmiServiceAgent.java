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

package org.robotframework.jvmconnector.agent;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

import org.robotframework.jvmconnector.server.RmiService;

public class RmiServiceAgent {
	private static String pathSeparator = System.getProperty("path.separator");
    private static String tmpDir = System.getProperty("java.io.tmpdir");
    private static String fileSeparator = System.getProperty("file.separator");

    public static void premain(String agentArguments, Instrumentation inst) {
        setClasspath(agentArguments, inst);
        startRmiService();
	}

    private static void setClasspath(String agentArguments, final Instrumentation inst) {
        new JarFinder(agentArguments).each(new JarFileAction() {
            public void doOnFile(JarFile file) {
                inst.appendToSystemClassLoaderSearch(file);
            }
        });
    }

    private static void startRmiService() {
        new RmiService().start(tmpDir + fileSeparator + "launcher.txt");
    }
}