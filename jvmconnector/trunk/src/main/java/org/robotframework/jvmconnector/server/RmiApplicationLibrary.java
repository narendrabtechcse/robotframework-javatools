package org.robotframework.jvmconnector.server;
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


public class RmiApplicationLibrary {
    private final ApplicationExecutor executor;

    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Usage: java RmiServiceLibrary [jvmArgs] rmiConfigFilePath applicationClassName [applicationArgs]");
        
        RmiApplicationLibrary rmiServiceLibrary = new RmiApplicationLibrary();
        rmiServiceLibrary.startRmiService(args[0]);
        rmiServiceLibrary.startSUT(args[1], rmiServiceLibrary.extractRestOfTheArgs(args));
    }

    public RmiApplicationLibrary() {
        this("java");
    }
    
    public RmiApplicationLibrary(String java) {
        this(java, "");
    }

    public RmiApplicationLibrary(String java, String jvmArgs) {
        this(new DefaultExecutor(java, jvmArgs, RmiApplicationLibrary.class));
    }
    
    RmiApplicationLibrary(ApplicationExecutor executor) {
        this.executor = executor;
    }

    public void startApplicationAndRMIService(String rmiConfigFilePath, String applicationClassName, String[] args) {
        executor.start(rmiConfigFilePath, applicationClassName, args);
    }
    
    private void startRmiService(final String configFilePath) {
//        logger.info("starting rmi service from " + configFilePath);
//        assertFileExist(configFilePath);
//        new FileSystemXmlApplicationContext(configFilePath);
    }

    private void assertFileExist(String configFilePath) {
//        Assert.assertTrue("File " + configFilePath + " doesn't exist.", new File(configFilePath).exists()); 
    }
    
    private void startSUT(String applicationClassName, String[] args) throws Exception {
//        logger.info("starting SUT " + applicationClassName + " with args " + Arrays.asList(args));
//        Method method = Class.forName(applicationClassName).getMethod("main", new Class[] { String[].class });
//        method.invoke(null, new Object[] { args });
    }
    
    private String[] extractRestOfTheArgs(String[] args) {
        throw new UnsupportedOperationException("not implemented.");
//        return ArrayUtil.<String>copyOfRange(args, 2, args.length);
    }
    
    private String toString(String[] args) {
        throw new UnsupportedOperationException("not implemented.");
//        StringBuffer argBuf = new StringBuffer();
//        for (int i = 0; i < args.length; i++) {
//            argBuf.append(args[i] + " ");
//        }
//        return argBuf.toString();
    }
}
