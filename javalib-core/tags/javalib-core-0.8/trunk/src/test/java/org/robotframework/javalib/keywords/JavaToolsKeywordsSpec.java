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


package org.robotframework.javalib.keywords;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.junit.runner.RunWith;
import org.robotframework.javalib.keywords.JavaToolsKeywords;
import org.robotframework.jdave.contract.RobotKeywordContract;
import org.robotframework.jdave.contract.RobotKeywordsContract;

@RunWith(JDaveRunner.class)
public class JavaToolsKeywordsSpec extends Specification<JavaToolsKeywords> {
    public class Any {
        public JavaToolsKeywords create() {
            return new JavaToolsKeywords();
        }

        public void isRobotKeywordsAnnotated() {
            specify(context, satisfies(new RobotKeywordsContract()));
        }
        
        public void hasSetSystemPropertyKeyword() {
            specify(context, satisfies(new RobotKeywordContract("setSystemProperty")));
        }
        
        public void hasGetSystemPropertyKeyword() {
            specify(context, satisfies(new RobotKeywordContract("getSystemProperty")));
        }
        
        public void setsSystemProperties() {
            context.setSystemProperty("someProperty", "someValue");
            specify(System.getProperty("someProperty"), "someValue");
        }
        
        public void getsSystemProperties() {
            System.setProperty("someProperty", "otherValue");
            specify(context.getSystemProperty("someProperty"), "otherValue");
        }
    }
}
