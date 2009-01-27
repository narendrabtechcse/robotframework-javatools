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

import java.io.IOException;

import org.robotframework.javalib.util.ArrayUtil;
import org.robotframework.jvmconnector.server.ApplicationLauncher;
import org.robotframework.jvmconnector.server.RmiService;

public class RmiApplicationLibrary {
    private static RmiService rmiService = new RmiService();
    private static ApplicationLauncher applicationLauncher = new ApplicationLauncher();
    
    private final String javaExecutable;
    private final String jvmArgs;

    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Usage: java RmiServiceLibrary [jvmArgs] rmiConfigFilePath applicationClassName [applicationArgs]");
        
        rmiService.start(args[0]);
        applicationLauncher.launchApplication(args[1], extractRestOfTheArgs(args));
    }

    public RmiApplicationLibrary() {
        this("java");
    }
    
    public RmiApplicationLibrary(String javaExecutable) {
        this(javaExecutable, "");
    }

    public RmiApplicationLibrary(String javaExecutable, String jvmArgs) {
        this.javaExecutable = javaExecutable;
        this.jvmArgs = jvmArgs;
    }
    
    public void startApplicationAndRMIService(String rmiConfigFilePath, String applicationClassName, String[] args) {
        try {
            Runtime.getRuntime().exec(createSelfExecutableCommand(rmiConfigFilePath, applicationClassName, args));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createSelfExecutableCommand(String rmiConfigFilePath, String applicationClassName, String[] args) {
        return javaExecutable + " " + jvmArgs + " " + this.getClass().getName() + " " + rmiConfigFilePath + " " + applicationClassName + " " + toString(args);
    }

    private String toString(String[] args) {
        StringBuilder argBuf = new StringBuilder();
        for (String arg : args) {
            argBuf.append(arg + " ");
        }
        return argBuf.toString().trim();
    }
    
    private static String[] extractRestOfTheArgs(String[] args) {
        return ArrayUtil.<String>copyOfRange(args, 2, args.length);
    }
}
