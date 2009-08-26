package org.robotframework.jvmconnector.agent;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class RmiServiceAgentSpec extends Specification<RmiServiceAgent> {
    public class Any {
        public RmiServiceAgent create() {
            return new RmiServiceAgent();
        }
        
        public void foo() {
            
        }
    }
}
