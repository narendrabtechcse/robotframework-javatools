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

package org.robotframework.jvmconnector.launch;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.robotframework.javalib.util.Logger;
import org.robotframework.jvmconnector.launch.jnlp.JnlpEnhancer;

public class WebstartLauncher {
    private final String javawsExecutable;
    private JnlpEnhancer jnlpRunner;

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
    }
    
    public WebstartLauncher(String libraryResourceDir) {
        this(libraryResourceDir, "javaws");
    }

    public WebstartLauncher(String libraryResourceDir, String javawsExecutable) {
        this.jnlpRunner = new JnlpEnhancer(libraryResourceDir);
        this.javawsExecutable = javawsExecutable;
    }

    public void startWebstartApplicationAndRmiService(String rmiPort, String jnlpUrl) throws Exception {
        String pathToJnlp = jnlpRunner.createRmiEnhancedJnlp(rmiPort, jnlpUrl);
        launchRmiEnhancedJnlp(pathToJnlp);
    }
    
    public String createRmiEnhancedJnlp(String rmiPort, String jnlpUrl) throws Exception {
        return jnlpRunner.createRmiEnhancedJnlp(rmiPort, jnlpUrl);
    }

    private Process launchRmiEnhancedJnlp(String jnlpFile) throws IOException {
        Logger.log("Path to jnlp: " + jnlpFile);
        return Runtime.getRuntime().exec(javawsExecutable + " " + jnlpFile);
    }
}
