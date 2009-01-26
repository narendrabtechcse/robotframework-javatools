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

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robotframework.javalib.util.ArrayUtil;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RmiServiceLibrary {
    private static Log logger = LogFactory.getLog(RmiServiceLibrary.class);
    static {
        logger.info("loading RmiServiceLibrary class");
    }
    {
        logger.info("instantiating RmiServiceLibrary class");
    }
    
    private final String java;
    private final String javaArgs;

    public static void main(String[] args) throws Exception {
        logger.info("------------------------------");
        logger.info(Arrays.asList(args));
        
        if (args.length < 2)
            throw new IllegalArgumentException("Usage: java RmiServiceLibrary [javaArgs] rmiConfigFilePath applicationClassName [applicationArgs]");
        
        RmiServiceLibrary rmiServiceLibrary = new RmiServiceLibrary();
        rmiServiceLibrary.startRmiService(args[0]);
        rmiServiceLibrary.startSUT(args[1], rmiServiceLibrary.extractRestOfTheArgs(args));
    }

    public RmiServiceLibrary() {
        this("java");
    }
    
    public RmiServiceLibrary(String java) {
        this(java, "");
    }

    public RmiServiceLibrary(String java, String javaArgs) {
        this.java = java;
        this.javaArgs = javaArgs;
    }

    public void startApplicationAndRMIService(String rmiConfigFilePath, String applicationClassName, String[] args) {
        logger.info(java + " " + javaArgs + " " + rmiConfigFilePath + " " + applicationClassName + " " + toString(args));
        try {
            Runtime.getRuntime().exec(
                java + " " + javaArgs + " " + this.getClass().getName() + " " + rmiConfigFilePath + " " + applicationClassName + " " + toString(args));
        } catch (Exception e) {
            logger.error("Problem encountered while trying to run " + applicationClassName, e);
        }
    }
    
    private void startRmiService(final String configFilePath) {
        logger.info("startRmiService START");
        new FileSystemXmlApplicationContext(configFilePath);
    }

    private void startSUT(String applicationClassName, String[] args) throws Exception {
        logger.info("starting SUT [" + applicationClassName + " with args " + Arrays.asList(args));
        Method method = Class.forName(applicationClassName).getMethod("main", new Class[] { String[].class });
        method.invoke(null, new Object[] { args });
    }
    
    private String[] extractRestOfTheArgs(String[] args) {
        return (String[]) ArrayUtil.copyOfRange(args, 2, args.length);
    }
    
    private String toString(String[] args) {
        StringBuffer argBuf = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            argBuf.append(args[i] + " ");
        }
        return argBuf.toString();
    }
}
