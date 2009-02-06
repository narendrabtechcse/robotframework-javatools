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

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.jmock.Expectations;
import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class JNLPSpec extends Specification<JNLP> {
    private Element jnlpRootElement;
    private Element appDesc;
    private String mainClass = "com.foo.SomeClass";
    
    public class WhenMainClassIsInJNLP {
        public JNLP create() {
            jnlpRootElement = mock(Element.class);
            appDesc = mock(Element.class, "appDesc");
            
            return new JNLP(jnlpRootElement);
        }
        
        public void knowsItsMainClass() {
            checking(new Expectations() {{
                atLeast(1).of(jnlpRootElement).getFirstChildElement("application-desc"); will(returnValue(appDesc));
                atLeast(1).of(appDesc).getAttributeValue("main-class"); will(returnValue(mainClass));
            }});
            
            specify(context.getMainClass(), mainClass);
        }
    }
    
    public class WhenMainClassIsNotInJNLP {
        private JarExtractor jarExtractor;

        public JNLP create() {
            jnlpRootElement = mock(Element.class);
            jarExtractor = mock(JarExtractor.class);
            appDesc = mock(Element.class, "appDesc");
            
            return new JNLP(jnlpRootElement, jarExtractor);
        }
        
        public void knowsItsMainClass() {
            final Jar mainJar = mock(Jar.class);
            
            checking(new Expectations() {{
                atLeast(1).of(jnlpRootElement).getFirstChildElement("application-desc"); will(returnValue(appDesc));
                atLeast(1).of(appDesc).getAttributeValue("main-class"); will(returnValue(null));
                
                one(jarExtractor).createMainJar(jnlpRootElement); will(returnValue(mainJar));
                one(mainJar).getMainClass(); will(returnValue(mainClass));
            }});
            
            specify(context.getMainClass(), mainClass);
        }
    }
}
