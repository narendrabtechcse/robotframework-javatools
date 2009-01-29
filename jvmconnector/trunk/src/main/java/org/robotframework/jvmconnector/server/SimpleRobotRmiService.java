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

package org.robotframework.jvmconnector.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robotframework.javalib.library.RobotJavaLibrary;
import org.robotframework.javalib.util.StdStreamRedirecter;
import org.robotframework.jvmconnector.common.KeywordExecutionResult;
import org.robotframework.jvmconnector.common.TestFailedException;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyValue;


/**
 * Robot RMI service that handles the communication between the RMI client and
 * the org.robotframework.javalib.library.RobotJavaLibrary.
 */
public class SimpleRobotRmiService implements RobotRmiService {
    private static Log logger = LogFactory.getLog(SimpleRobotRmiService.class);
    
    private RobotJavaLibrary library;

    private StdStreamRedirecter streamRedirecter;

    public SimpleRobotRmiService() {
        this(new StdStreamRedirecter());
    }

    /**
     * @param streamRedirecter
     *            StdStreamRedirecter that handles the redirection of
     *            STDOUT and STDERR.
     */
    public SimpleRobotRmiService(StdStreamRedirecter streamRedirecter) {
        this.streamRedirecter = streamRedirecter;
        logger.info("!!! Instantiating SimpleRobotRmiService.");
    }

    public String[] getKeywordNames() {
        return library.getKeywordNames();
    }

    public void setLibrary(RobotJavaLibrary library) {
        this.library = library;
    }

    public void setLibraryProperties(String properties) {
        PropertyValue[] propertyValues = getPropertyValues(properties);
        PropertyAccessor robotLibraryPropertyAccessor = getLibraryPropertyAccessor();

        for (int i = 0; i < propertyValues.length; ++i)
            robotLibraryPropertyAccessor.setPropertyValue(propertyValues[i]);
    }

    public KeywordExecutionResult runKeyword(String keywordName, Object[] keywordArguments) {
        streamRedirecter.redirectStdStreams();
        KeywordExecutionResult keywordExecutionResult = getExecutionResults(keywordName,
                keywordArguments);
        streamRedirecter.resetStdStreams();
        return keywordExecutionResult;
    }

    private KeywordExecutionResult getExecutionResults(String keywordName, Object[] keywordArguments) {
        KeywordExecutionResultImpl keywordExecutionResult = new KeywordExecutionResultImpl();
        try {
            keywordExecutionResult.setResult(library.runKeyword(keywordName, keywordArguments));
        } catch (Throwable e) {
            keywordExecutionResult.setTestFailedException(new TestFailedException(e));
        }
        keywordExecutionResult.setStdStreams(streamRedirecter);
        return keywordExecutionResult;
    }

    protected PropertyAccessor getLibraryPropertyAccessor() {
        return new BeanWrapperImpl(library);
    }

    protected PropertyValue[] getPropertyValues(String propertyPattern) {
        return new PropertyParser(propertyPattern).getPropertyValues();
    }
}
