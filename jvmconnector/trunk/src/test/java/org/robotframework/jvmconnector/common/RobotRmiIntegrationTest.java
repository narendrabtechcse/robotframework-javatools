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

package org.robotframework.jvmconnector.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.robotframework.jvmconnector.common.PropertyParsingFailedException;
import org.robotframework.jvmconnector.common.TestFailedException;
import org.robotframework.jvmconnector.mocks.ExceptionThrowingKeyword;
import org.robotframework.jvmconnector.mocks.LoggingKeyword;
import org.robotframework.jvmconnector.mocks.MockException;
import org.robotframework.jvmconnector.mocks.MockJavaLibrary;
import org.robotframework.jvmconnector.mocks.PropertyShouldBeSetToRmiService;
import org.robotframework.jvmconnector.util.RmiHelperUtil;
import org.robotframework.jvmconnector.util.RmiHelperUtil.FakeRmiClient;
import org.springframework.context.support.GenericApplicationContext;


public class RobotRmiIntegrationTest extends TestCase {
    private GenericApplicationContext serverAppCtx;
    private GenericApplicationContext clientBeanFactory;
    private FakeRmiClient rmiClient;

    public RobotRmiIntegrationTest() {
        int freePort = RmiHelperUtil.getFreePort();
        serverAppCtx = RmiHelperUtil.getServerApplicationContext(freePort);
        clientBeanFactory = RmiHelperUtil.getClientBeanFactory(freePort);
    }

    public void setUp() {
        serverAppCtx.refresh();
        rmiClient = new FakeRmiClient(clientBeanFactory);
    }

    public void tearDown() throws Exception {
        serverAppCtx.close();
        serverAppCtx.destroy();
    }

    public void testClientFindsKeywordsFromTheLibraryInAppContext() {
        String[] keywords = rmiClient.getKeywordNames();
        assertArraysContainSame(new MockJavaLibrary().getKeywordNames(), keywords);
    }

    public void testClientRunsKeywordFromTheLibrary() {
        Object keywordRetVal = rmiClient.runKeyword(LoggingKeyword.KEYWORD_NAME, null);
        assertEquals(LoggingKeyword.RETURN_VALUE, keywordRetVal);
    }

    public void testPrintsKeywordStdOutLoggingToStdOut() {
        rmiClient.runKeyword(LoggingKeyword.KEYWORD_NAME, null);
        assertEquals(LoggingKeyword.LOG_STRING_STDOUT, rmiClient.getStdOutAsString());
    }

    public void testPrintsKeywordStdErrLoggingToStdErr() {
        rmiClient.runKeyword(LoggingKeyword.KEYWORD_NAME, null);
        assertEquals(LoggingKeyword.LOG_STRING_STDERR, rmiClient.getStdErrAsString());
    }

    public void testExceptionReturnedByKeywordExecutionResultIsThrown() {
        try {
            rmiClient.runKeyword(ExceptionThrowingKeyword.KEYWORD_NAME, null);
            fail("Excpected testFailedException to be thrown");
        } catch (TestFailedException e) {}
    }

    public void testSettingPropertyToLibraryEnablesKeyword() {
        assertKeywordNotFoundBeforePropertySet();
        assertKeywordFoundAfterPropertySet();
    }

    public void testSettingBadPropertyPatternToLibraryThrowsException() {
        try {
            new RmiHelperUtil.FakeRmiClient(clientBeanFactory, "bad property pattern");
            fail("Expected PropertyParsingFailedException to be thrown");
        } catch (PropertyParsingFailedException e) {}
    }

    private void assertKeywordFoundAfterPropertySet() {
        FakeRmiClient mockRmiClientWithPattern = new RmiHelperUtil.FakeRmiClient(clientBeanFactory,
            MockJavaLibrary.PATTERN_KEYWORD_PROPERTY_NAME + "=" + MockJavaLibrary.PATTERN_KEYWORD_PROPERTY_VALUE);

        assertEquals(Boolean.TRUE, mockRmiClientWithPattern.runKeyword(PropertyShouldBeSetToRmiService.KEYWORD_NAME, null));
    }

    private void assertKeywordNotFoundBeforePropertySet() {
        boolean keywordFound = true;
        try {
            rmiClient.runKeyword(PropertyShouldBeSetToRmiService.KEYWORD_NAME, null);
        } catch (TestFailedException e) {
            if (e.getSourceExceptionClassName().equals(MockException.class.getName()))
                keywordFound = false;
        }

        assertFalse("Excpected the keyword '" + PropertyShouldBeSetToRmiService.KEYWORD_NAME + "' not to be found", keywordFound);
    }

    private void assertArraysContainSame(Object[] expectedAr, Object[] ar) {
        Set expectedContents = new HashSet(Arrays.asList(expectedAr));
        Set contents = new HashSet(Arrays.asList(ar));
        assertEquals(expectedContents, contents);
    }
}
