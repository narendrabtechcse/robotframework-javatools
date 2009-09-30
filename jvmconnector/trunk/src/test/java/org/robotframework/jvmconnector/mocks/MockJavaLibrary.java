package org.robotframework.jvmconnector.mocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.robotframework.javalib.keyword.DocumentedKeyword;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.jvmconnector.xmlrpc.RobotLibrary;

public class MockJavaLibrary implements RobotLibrary  {
    public static final String PATTERN_KEYWORD_PROPERTY_NAME = "someProperty";
    public static final String PATTERN_KEYWORD_PROPERTY_VALUE = "patternKeyword";

    @SuppressWarnings("serial")
    public Map<String, Keyword> keywords = new HashMap<String, Keyword>() {{
        put("concatenatingKeyword", new ConcatenatingKeyword());
        put(LoggingKeyword.KEYWORD_NAME, new LoggingKeyword());
        put(ExceptionThrowingKeyword.KEYWORD_NAME, new ExceptionThrowingKeyword());
    }};

    public String[] getKeywordNames() {
        return (String[]) keywords.keySet().toArray(new String[0]);
    }

    public Object runKeyword(String keywordName, Object[] args) {
        System.out.println("Running keyword " + keywordName + " with args: " + Arrays.asList(args));
        Keyword keyword = (Keyword) keywords.get(keywordName);
        if (keyword == null)
            throw new MockException("Failed to find keyword '" + keywordName + "'");

        return keyword.execute(args);
    }

    public void setSomeProperty(String somePropertyValue) {
        if (PATTERN_KEYWORD_PROPERTY_VALUE.equalsIgnoreCase(somePropertyValue))
            keywords.put(PropertyShouldBeSetToRmiService.KEYWORD_NAME, new PropertyShouldBeSetToRmiService());
    }
    
    public String[] getKeywordArguments(String keywordName) {
        Keyword keyword = keywords.get(keywordName);
        if (keyword != null && DocumentedKeyword.class.isAssignableFrom(keyword.getClass())) {
            return ((DocumentedKeyword) keyword).getArgumentNames();
        }
        return new String[0];
    }

    public String getKeywordDocumentation(String keywordName) {
        Keyword keyword = keywords.get(keywordName);
        if (keyword != null && DocumentedKeyword.class.isAssignableFrom(keyword.getClass())) {
            return ((DocumentedKeyword) keyword).getDocumentation();
        }
        return "";
    }
}
