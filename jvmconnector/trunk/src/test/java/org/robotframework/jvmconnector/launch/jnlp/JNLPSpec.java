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


package org.robotframework.jvmconnector.launch.jnlp;

import java.util.jar.JarFile;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.robotframework.javalib.beans.common.URLFileFactory;

@RunWith(JDaveRunner.class)
public class JNLPSpec extends Specification<JNLP> {
    private JNLPElement jnlpRootElement;
    private JNLPElement appDesc;
    private String mainClass = "com.foo.SomeClass";
    
    public class WhenMainClassIsInJNLP {
        public JNLP create() {
            jnlpRootElement = mock(JNLPElement.class);
            appDesc = mock(JNLPElement.class, "appDesc");
            
            return new JNLP(jnlpRootElement);
        }
        
        public void knowsItsMainClass() {
            checking(new Expectations() {{
                one(jnlpRootElement).getFirstChildElement("application-desc"); will(returnValue(appDesc));
                one(appDesc).getAttributeValue("main-class"); will(returnValue(mainClass));
            }});
            
            specify(context.getMainClass(), mainClass);
        }
    }
    
    public class WhenMainClassIsNotInJNLP {
        private URLFileFactory fileFactory;

        public JNLP create() {
            jnlpRootElement = mock(JNLPElement.class);
            fileFactory = mock(URLFileFactory.class);
            appDesc = mock(JNLPElement.class, "appDesc");
            
            
            return new JNLP(jnlpRootElement, fileFactory);
        }
        
        public void knowsItsMainClass() {
            final JNLPElement resources = mock(JNLPElement.class, "resources");
            final JNLPElement firstJar = mock(JNLPElement.class, "firstJar");
            final JarFile jarFile = mock(JarFile.class);
            
            checking(new Expectations() {{
                one(jnlpRootElement).getFirstChildElement("application-desc"); will(returnValue(appDesc));
                one(appDesc).getAttributeValue("main-class"); will(returnValue(null));
                
                one(jnlpRootElement).getAttributeValue("codebase"); will(returnValue("http://somehost"));
                one(jnlpRootElement).getFirstChildElement("resources"); will(returnValue(resources));
                one(resources).getFirstChildElement("jar"); will(returnValue(firstJar));
                one(firstJar).getAttributeValue("href"); will(returnValue("someJar.jar"));
                
                one(fileFactory).createFileFromUrl("http://somehost/someJar.jar");
                will(returnValue(jarFile));
            }});
            
            
        }
    }
}
