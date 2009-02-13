package org.robotframework.javalib.beans.annotation;

import java.util.HashMap;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.laughingpanda.beaninject.Inject;
import org.robotframework.javalib.beans.common.IClassFilter;
import org.robotframework.javalib.beans.common.IKeywordBeanDefintionReader;
import org.springframework.context.support.GenericApplicationContext;

public class KeywordBeanLoaderTest extends MockObjectTestCase {
    private IClassFilter classFilter = (IClassFilter) mock(IClassFilter.class).proxy();
    private Mock appContext = mock(GenericApplicationContext.class);
    private HashMap keywordBeans = new HashMap() {{ put("keywordName", new Object()); }};

    public void testLoadsClassesFromApplicationContext() throws Exception {
        IBeanLoader beanLoader = createBeanLoaderWithMockBeanDefinitionReader("org/**/**.class");
        assertSame(keywordBeans, beanLoader.loadBeanDefinitions(classFilter));
    }

    public void testUsesGivenKeywordPatternAndExtractsPackagePrefixFromIt() throws Exception {
        IBeanLoader beanLoaderWithKeywordPattern = createBeanLoaderWithMockBeanDefinitionReader("some/package/*.class");

        beanLoaderWithKeywordPattern.loadBeanDefinitions(classFilter);
    }
    
    public void usesGivenClassLoader() {
        ClassLoader classLoader = (ClassLoader) mock(ClassLoader.class).proxy();
        new KeywordBeanLoader("");
    }

    private IBeanLoader createBeanLoaderWithMockBeanDefinitionReader(String keywordPattern) {
        IBeanLoader beanLoader = createBeanLoaderWithMockApplicationContext(keywordPattern);
        Mock beanDefinitionReader = createMockBeanDefinitionReader(keywordPattern);
        Inject.field("beanDefinitionReader").of(beanLoader).with(beanDefinitionReader.proxy());
        return beanLoader;
    }

    private Mock createMockBeanDefinitionReader(String keywordPattern) {
        Mock beanDefinitionReader = mock(IKeywordBeanDefintionReader.class);
        beanDefinitionReader.expects(once()).method("loadBeanDefinitions")
            .with(eq(keywordPattern), same(classFilter))
            .will(returnValue(3));
        return beanDefinitionReader;
    }

    private IBeanLoader createBeanLoaderWithMockApplicationContext(String keywordPattern) {
        IBeanLoader beanLoader = new KeywordBeanLoader(keywordPattern);
        injectMockAppContextTo(beanLoader);
        return beanLoader;
    }

    private void injectMockAppContextTo(IBeanLoader beanLoader) {
        appContext.stubs();
        appContext.expects(once()).method("refresh");
        appContext.expects(once()).method("getBeansOfType")
            .with(eq(Object.class))
            .will(returnValue(keywordBeans));
        Inject.field("context").of(beanLoader).with(appContext.proxy());
    }
}
