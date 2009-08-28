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

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.jmock.Expectations;
import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class Java6ClassPathAppenderSpec extends Specification<Java6ClassPathAppender> {
    public class Any {
        private Instrumentation mustangInstrumentation;
        private Java6ClassPathAppender classPathAppender;

        public Java6ClassPathAppender create() {
            mustangInstrumentation = mock(Instrumentation.class);
            
            classPathAppender = new Java6ClassPathAppender(mustangInstrumentation);
            return classPathAppender;
        }
        
        public void appendsToClasspathWithMustang() {
            final JarFile jarFile = dummy(JarFile.class);
            checking(new Expectations() {{
                one(mustangInstrumentation).appendToSystemClassLoaderSearch(jarFile);
            }});
            
            classPathAppender.appendToClasspath(jarFile);
        }
    }
}
