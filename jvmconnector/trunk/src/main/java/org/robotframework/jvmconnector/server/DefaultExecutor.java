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


package org.robotframework.jvmconnector.server;

import java.io.IOException;

public class DefaultExecutor implements ApplicationExecutor {
    private final String javaExecutable;
    private final String jvmArgs;
    private final Class<?> rmiServiceWrapper;

    public DefaultExecutor(String javaExecutable, String jvmArgs, Class<?> rmiServiceWrapper) {
        this.javaExecutable = javaExecutable;
        this.jvmArgs = jvmArgs;
        this.rmiServiceWrapper = rmiServiceWrapper;
    }

    public void start(String rmiConfigFilePath, String applicationClassName, String[] args) {
        try {
            Runtime.getRuntime().exec(createCommand(rmiConfigFilePath, applicationClassName, args));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createCommand(String rmiConfigFilePath, String applicationClassName, String[] args) {
        return javaExecutable + " " + jvmArgs + " " + rmiServiceWrapper.getName() + " " + rmiConfigFilePath + " " + applicationClassName + " " + toString(args);
    }

    private String toString(String[] args) {
        StringBuilder argBuf = new StringBuilder();
        for (String arg : args) {
            argBuf.append(arg + " ");
        }
        return argBuf.toString().trim();
    }
}
