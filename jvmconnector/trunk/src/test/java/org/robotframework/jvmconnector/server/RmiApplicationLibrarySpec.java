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

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.laughingpanda.beaninject.Inject;

@RunWith(JDaveRunner.class)
public class RmiApplicationLibrarySpec extends Specification<RmiApplicationLibrary> {
    private String javaExecutable = "java";
    private String jvmArgs = "-Dfoo=bar";
    private String applicationClassName = "com.acme.SomeApp";
    private String rmiConfigFilePath = "rmiConfig.xml";
    
    public class ActingAsLibrary {
        public RmiApplicationLibrary create() {
            return new RmiApplicationLibrary(javaExecutable, jvmArgs);
        }
        
        public void executesItselfWithGivenArgs() throws Exception {
            final Runtime runtime = mock(Runtime.class);
            Inject.staticField("currentRuntime").of(Runtime.class).with(runtime);
            
            String applicationArgs = "foo bar baz";
            
            final String expectedCommand = javaExecutable + " " + jvmArgs + " " + RmiApplicationLibrary.class.getName()
                + " " + rmiConfigFilePath + " " + applicationClassName + " " + applicationArgs;
            
            checking(new Expectations() {{
                one(runtime).exec(expectedCommand);
            }});
            
            context.startApplicationAndRMIService(rmiConfigFilePath, applicationClassName, new String[] {"foo", "bar", "baz" });
        }
    }
    
    public class WhenExecuted {
        private RmiService rmiService;
        private ApplicationLauncher applicationLauncher;

        public void create() {
            rmiService = injectMock(RmiService.class, "rmiService", RmiApplicationLibrary.class);
            applicationLauncher = injectMock(ApplicationLauncher.class, "applicationLauncher", RmiApplicationLibrary.class);
        }
        
        public void startsRmiService() throws Exception {
            checking(new Expectations() {{
                one(rmiService).start(rmiConfigFilePath);
                ignoring(applicationLauncher);
            }});
            
            RmiApplicationLibrary.main(new String[] {rmiConfigFilePath, applicationClassName });
        }
        
        public void startsApplication() throws Exception {
            checking(new Expectations() {{
                one(applicationLauncher).launchApplication(applicationClassName, new String[] {"foo", "bar"});
                ignoring(rmiService);
            }});
            
            RmiApplicationLibrary.main(new String[] {rmiConfigFilePath, applicationClassName, "foo", "bar"});
        }
        
        private <T> T injectMock(Class<T> mockedClass, String fieldName, Class<?> target) {
            T mockedDependency = mock(mockedClass);
            Inject.staticField(fieldName).of(target).with(mockedDependency);
            return mockedDependency;
        }
    }
}
