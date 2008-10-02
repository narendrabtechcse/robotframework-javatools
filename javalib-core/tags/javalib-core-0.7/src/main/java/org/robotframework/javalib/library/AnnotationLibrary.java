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

package org.robotframework.javalib.library;

import java.lang.reflect.InvocationTargetException;

import org.robotframework.javalib.beans.annotation.AnnotationBasedKeywordFilter;
import org.robotframework.javalib.beans.annotation.IBeanLoader;
import org.robotframework.javalib.beans.annotation.KeywordBeanLoader;
import org.robotframework.javalib.beans.common.IClassFilter;
import org.robotframework.javalib.factory.AnnotationKeywordFactory;
import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.keyword.DocumentedKeyword;

/**
 * @author Heikki Hulkko
 */
public class AnnotationLibrary extends KeywordFactoryBasedLibrary<DocumentedKeyword> implements KeywordDocumentationRepository {
    private IBeanLoader beanLoader;
    private IClassFilter classFilter = new AnnotationBasedKeywordFilter();
    private KeywordFactory<DocumentedKeyword> keywordFactory;

    public AnnotationLibrary() {}

    public AnnotationLibrary(String keywordPattern) {
        setKeywordPattern(keywordPattern);
    }

    @Override
    protected KeywordFactory<DocumentedKeyword> createKeywordFactory() {
        assumeKeywordPatternIsSet();
        if (keywordFactory == null) {
            keywordFactory = new AnnotationKeywordFactory(beanLoader.loadBeanDefinitions(classFilter));
        }
        return keywordFactory;
    }

    public String[] getKeywordArguments(String keywordName) {
        return createKeywordFactory().createKeyword(keywordName).getArgumentNames();
    }

    public String getKeywordDocumentation(String keywordName) {
        return createKeywordFactory().createKeyword(keywordName).getDocumentation();
    }

    @Override
    public Object runKeyword(String keywordName, Object[] args) {
        try {
            return super.runKeyword(keywordName, args);
        } catch (RuntimeException e) {
            throw retrieveInnerException(e);
        }
    }

    public void setKeywordPattern(String keywordPattern) {
        beanLoader = new KeywordBeanLoader(keywordPattern);
    }

    private void assumeKeywordPatternIsSet() {
        if (beanLoader == null) {
            throw new IllegalStateException("Keyword pattern must be set before calling getKeywordNames.");
        }
    }

    private RuntimeException retrieveInnerException(RuntimeException e) {
        Throwable cause = e.getCause();
        if (InvocationTargetException.class.equals(cause.getClass())) {
            Throwable original = cause.getCause();
            return new RuntimeException(original.getMessage(), original);
        }

        return e;
    }
}
