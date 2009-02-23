package org.robotframework.javalib.library;

import org.jmock.MockObjectTestCase;
import org.laughingpanda.beaninject.impl.Accessor;
import org.robotframework.javalib.beans.annotation.KeywordBeanLoader;


public class AnnotationLibraryTest extends MockObjectTestCase {
    private AnnotationLibrary annotationLibrary;
    private String keywordPattern = "somePattern";
    private KeywordBeanLoader beanLoaderAtInitialization;
    private KeywordBeanLoader beanLoaderAfterSettingKeywordPattern;

    protected void setUp() throws Exception {
        annotationLibrary = new AnnotationLibrary();
        beanLoaderAtInitialization = extractBeanLoaderFromAnnotationLibrary();
        annotationLibrary.setKeywordPattern(keywordPattern);
        beanLoaderAfterSettingKeywordPattern = extractBeanLoaderFromAnnotationLibrary();
    }

    public void testThrowsExceptionIfKeywordPatternIsNotSet() throws Exception {
        try {
            new AnnotationLibrary().getKeywordNames();
            fail("Expected IllegalStateException to be thrown.");
        } catch (IllegalStateException e) {
            assertEquals("Keyword pattern must be set before calling getKeywordNames.", e.getMessage());
        }
    }

    public void testCreatesNewBeanLoaderWhenKeywordPatternSet() throws Exception {
        assertNotSame(beanLoaderAtInitialization, beanLoaderAfterSettingKeywordPattern);
    }

    public void testSetsKeywordPatternToBeanLoader() throws Exception {
        String extractedKeywordPattern = extractKeywordPatternFrom(beanLoaderAfterSettingKeywordPattern);
        assertEquals(keywordPattern, extractedKeywordPattern);
    }

    private String extractKeywordPatternFrom(KeywordBeanLoader beanLoader) throws IllegalAccessException {
        return (String) Accessor.field("keywordPattern", beanLoader.getClass()).get(beanLoader);
    }

    private KeywordBeanLoader extractBeanLoaderFromAnnotationLibrary() throws IllegalAccessException {
        return (KeywordBeanLoader) Accessor.field("beanLoader", annotationLibrary.getClass()).get(annotationLibrary);
    }
}
