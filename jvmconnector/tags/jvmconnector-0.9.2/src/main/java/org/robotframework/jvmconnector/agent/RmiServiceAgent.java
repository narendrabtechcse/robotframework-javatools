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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

import org.robotframework.jvmconnector.server.RmiService;

public class RmiServiceAgent {
    private static final String LOG_FILE = "/tmp/lol.log";

    static {
        new File(LOG_FILE).delete();
    }
    
    private static String tmpDir = System.getProperty("java.io.tmpdir");
    private static String pathSeparator = System.getProperty("path.separator");
    private static String fileSeparator = System.getProperty("file.separator");
    
    private static ClassPathAppenderFactory appenderFactory = new ClassPathAppenderFactory();

    public static void premain(String agentArguments, Instrumentation inst) {
        log("1: " + agentArguments);
        String javaToolOption = System.getenv("JAVA_TOOL_OPTIONS");
        log("javaToolOption: " + javaToolOption);
        setClasspath(agentArguments, inst);
        startRmiService();
	}

    static void setClasspath(String agentArguments, final Instrumentation inst) {
        for (String file : split(agentArguments)) {
            log("2: " + file);
            addJarsToClasspath(inst, file);
        }
    }

    private static String[] split(String agentArguments) {
        if (agentArguments == null) return new String[0];
        return agentArguments.split(pathSeparator);
    }

    private static void addJarsToClasspath(final Instrumentation inst, String file) {
        final StringBuilder sb = new StringBuilder();
        
        new JarFinder(file).each(new JarFileAction() {
            public void doOnFile(JarFile file) {
                sb.append(file.getName() + "\n");
                addToClassPath(inst, file);
            }
        });
        
        log("3: " + sb.toString());
    }
    
    private static void addToClassPath(Instrumentation inst, JarFile file) {
        classPathAppender(inst).appendToClasspath(file);
    }

    private static ClassPathAppender classPathAppender(Instrumentation inst) {
        return appenderFactory.create(inst);
    }

    private static void startRmiService() {
        new RmiService().start(tmpDir + fileSeparator + "launcher.txt");
    }
    
    public static void log(String msg) {
        try {
            write(LOG_FILE, msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(String fileName, String msg) throws IOException {
        Writer writer = new FileWriter(fileName, true);
        writer.write(msg + "\n");
        writer.flush();
        writer.close();
    }
}
