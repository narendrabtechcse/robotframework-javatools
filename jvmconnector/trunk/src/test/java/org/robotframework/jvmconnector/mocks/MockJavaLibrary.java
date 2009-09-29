package org.robotframework.jvmconnector.mocks;

import java.util.HashMap;
import java.util.Map;

import org.laughingpanda.jretrofit.AllMethodsNotImplementedException;
import org.laughingpanda.jretrofit.Retrofit;
import org.robotframework.javalib.keyword.DocumentedKeyword;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.library.KeywordDocumentationRepository;
import org.robotframework.javalib.library.RobotJavaLibrary;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MockJavaLibrary implements RobotJavaLibrary, KeywordDocumentationRepository {
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
        System.out.println("Running keyword " + keywordName + " with args: " + Arrays.asList(args));
        Keyword keyword = (Keyword) keywordMap.get(keywordName);
        if (keyword == null)
            throw new MockException("Failed to find keyword '" + keywordName + "'");

        return keyword.execute(args);
    }

    public void setSomeProperty(String somePropertyValue) {
        if (PATTERN_KEYWORD_PROPERTY_VALUE.equalsIgnoreCase(somePropertyValue))
            keywordMap.put(PropertyShouldBeSetToRmiService.KEYWORD_NAME, new PropertyShouldBeSetToRmiService());
    }
    
    public String[] getKeywordArguments(String keywordName) {
        String[] argumentNames = toWithDocs(keywordName).getArgumentNames();
        System.out.println(Arrays.asList(argumentNames));
        return argumentNames;
    }

    public String getKeywordDocumentation(String keywordName) {
        return toWithDocs(keywordName).getDocumentation();
    }
    
    private DocumentedKeyword toWithDocs(String keywordName) {
        Keyword keyword = keywordMap.get(keywordName);
        try {
            return (DocumentedKeyword) Retrofit.complete(keyword, DocumentedKeyword.class);
        } catch (AllMethodsNotImplementedException e) {
            return new KeywordWithEmptyDocumentation(keyword);
        }
    }
}
