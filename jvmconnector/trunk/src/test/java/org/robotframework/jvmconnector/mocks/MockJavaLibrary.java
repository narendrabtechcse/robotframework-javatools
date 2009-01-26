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

package org.robotframework.jvmconnector.mocks;

import java.util.HashMap;
import java.util.Map;

import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.library.RobotJavaLibrary;

public class MockJavaLibrary implements RobotJavaLibrary {
    public static final String PATTERN_KEYWORD_PROPERTY_NAME = "someProperty";
    public static final String PATTERN_KEYWORD_PROPERTY_VALUE = "patternKeyword";

    public Map keywordMap = new HashMap() {
        {
            put(LoggingKeyword.KEYWORD_NAME, new LoggingKeyword());
            put(ExceptionThrowingKeyword.KEYWORD_NAME, new ExceptionThrowingKeyword());
            put(new ApplicationIsRunning().getClass().getSimpleName(), new ApplicationIsRunning());
            put(new StopApplication().getClass().getSimpleName(), new StopApplication());
        }
    };

    public String[] getKeywordNames() {
        return (String[]) keywordMap.keySet().toArray(new String[0]);
    }

    public Object runKeyword(String keywordName, Object[] args) {
        Keyword keyword = (Keyword) keywordMap.get(keywordName);
        if (keyword == null)
            throw new MockException("Failed to find keyword '" + keywordName + "'");

        return keyword.execute(args);
    }

    public void setSomeProperty(String somePropertyValue) {
        if (PATTERN_KEYWORD_PROPERTY_VALUE.equalsIgnoreCase(somePropertyValue))
            keywordMap.put(PropertyShouldBeSetToRmiService.KEYWORD_NAME, new PropertyShouldBeSetToRmiService());
    }
    
    public static void main(String[] args) {
        System.out.println(new MockJavaLibrary().getClass().getSimpleName());
    }
}
