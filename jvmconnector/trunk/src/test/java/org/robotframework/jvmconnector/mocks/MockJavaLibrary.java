package org.robotframework.jvmconnector.mocks;

import java.util.HashMap;
import java.util.Map;

import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.library.RobotJavaLibrary;

public class MockJavaLibrary implements RobotJavaLibrary {
    public static final String PATTERN_KEYWORD_PROPERTY_NAME = "someProperty";
    public static final String PATTERN_KEYWORD_PROPERTY_VALUE = "patternKeyword";

    @SuppressWarnings("serial")
    public Map<String, Keyword> keywordMap = new HashMap<String, Keyword>() {{
        put("concatenatingKeyword", new ConcatenatingKeyword());
        put(LoggingKeyword.KEYWORD_NAME, new LoggingKeyword());
        put(ExceptionThrowingKeyword.KEYWORD_NAME, new ExceptionThrowingKeyword());
    }};

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
