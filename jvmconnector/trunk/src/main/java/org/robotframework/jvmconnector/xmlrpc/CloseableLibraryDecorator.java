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

package org.robotframework.jvmconnector.xmlrpc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.laughingpanda.jretrofit.AllMethodsNotImplementedException;
import org.laughingpanda.jretrofit.Retrofit;
import org.robotframework.javalib.library.KeywordDocumentationRepository;
import org.robotframework.javalib.library.RobotJavaLibrary;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CloseableLibraryDecorator implements RobotLibrary {
    public static final String KEYWORD_CLOSE_APPLICATION = "closeapplication";
    private final RobotJavaLibrary library;

    public CloseableLibraryDecorator(RobotJavaLibrary library) {
        System.out.println("Wrapping library " + library.getClass().getName());
        this.library = library;
    }

    public String[] getKeywordNames() {
        List<String> newKeywordNames = new ArrayList<String>();
        CollectionUtils.addAll(newKeywordNames, library.getKeywordNames());
        newKeywordNames.add(KEYWORD_CLOSE_APPLICATION);
        System.out.println("getKeywordNames: " + newKeywordNames);
        return newKeywordNames.toArray(new String[0]);
    }

    public Object runKeyword(String keywordName, Object[] args) {
        System.out.println("runKeyword: " + keywordName + " with args " + Arrays.asList(args));
        if (KEYWORD_CLOSE_APPLICATION.equalsIgnoreCase(keywordName)) {
            shutdownInSeparateThread();
            return true;
        }
        return library.runKeyword(keywordName, args);
    }

    public String[] getKeywordArguments(String keywordName) {
        String[] keywordArguments = keywordInfo().getKeywordArguments(keywordName);
        System.out.println("getKeywordArguments: " + Arrays.asList(keywordArguments));
        return keywordArguments;
    }
    
    public String getKeywordDocumentation(String keywordName) {
        String keywordDocumentation = keywordInfo().getKeywordDocumentation(keywordName);
        System.out.println("getKeywordDocumentation: " + keywordDocumentation);
        return keywordDocumentation;
    }
    
    private void shutdownInSeparateThread() {
        new Thread() {
            public void run() {
                System.exit(0);
            }
        }.start();
    }

    private KeywordDocumentationRepository keywordInfo() {
        try {
            return (KeywordDocumentationRepository) Retrofit.complete(library, KeywordDocumentationRepository.class);
        } catch (AllMethodsNotImplementedException e) {
            return new NullDocumentationRepo();
        }
    }
}

class NullDocumentationRepo implements KeywordDocumentationRepository {
    public String[] getKeywordArguments(String keywordName) {
        return null;
    }

    public String getKeywordDocumentation(String keywordName) {
        return null;
    }
}