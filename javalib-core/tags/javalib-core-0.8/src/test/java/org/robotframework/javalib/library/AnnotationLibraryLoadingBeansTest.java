package org.robotframework.javalib.library;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.laughingpanda.beaninject.Inject;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.beans.annotation.IBeanLoader;
import org.robotframework.javalib.beans.common.IClassFilter;
import org.robotframework.javalib.util.ArrayUtil;

public class AnnotationLibraryLoadingBeansTest extends MockObjectTestCase {
    private AnnotationLibrary annotationLibrary = new AnnotationLibrary();

    public void testLoadsKeywordClassesWithBeanLoader() throws Exception {
        injectBeanDefinitionsToAnnotationLibrary();
        String[] expectedKeywordNames = new String[] { "someKeyword", "anotherKeyword" };
        ArrayUtil.assertArraysContainSame(expectedKeywordNames, annotationLibrary.getKeywordNames());
    }

    private void injectBeanDefinitionsToAnnotationLibrary() {
        Mock beanLoader = mock(IBeanLoader.class);
        Object classFilter = mock(IClassFilter.class).proxy();

        Inject.field("beanLoader").of(annotationLibrary).with(beanLoader.proxy());
        Inject.field("classFilter").of(annotationLibrary).with(classFilter);

        beanLoader.expects(once()).method("loadBeanDefinitions")
            .with(eq(classFilter))
            .will(returnValue(createKeywordBeans()));
    }

    private Map createKeywordBeans() {
        return new HashMap() {{
            put("keywordsBean1", new SomeKeywords());
            put("keywordsBean2", new AnotherKeywords());
        }};
    }

    @RobotKeywords
    private static class SomeKeywords {
        @RobotKeyword
        public void someKeyword() { }
    }

    @RobotKeywords
    private static class AnotherKeywords {
        @RobotKeyword
        public void anotherKeyword() { }
    }
}
