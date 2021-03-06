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

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.robotframework.javalib.library.RobotJavaLibrary;
import org.robotframework.javalib.util.StdStreamRedirecter;
import org.robotframework.jvmconnector.common.KeywordExecutionResult;
import org.robotframework.jvmconnector.mocks.ExceptionThrowingKeyword;
import org.robotframework.jvmconnector.mocks.LoggingKeyword;
import org.robotframework.jvmconnector.mocks.MockException;
import org.robotframework.jvmconnector.mocks.MockJavaLibrary;
import org.robotframework.jvmconnector.server.RobotRmiService;
import org.robotframework.jvmconnector.server.SimpleRobotRmiService;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyValue;


public class SimpleRobotRmiServerTest extends MockObjectTestCase {
	private Mock mockJavaLibrary;
	private Mock mockStreamRedirecter;
	private SimpleRobotRmiService robotRmiService;
	private String keywordName;
	private Object[] keywordArguments;
	private Object keywordReturnValue;
	
	public void setUp() throws Exception {
		setMockJavaLibrary();
		setMockStreamRedirecter();
		setRobotRmiService();
		
		keywordName = "some keyword";
		keywordArguments = new Object[0];
		keywordReturnValue = new Object();
	}
	
	public void testUsesRobotJavaLibraryForGettingKeywords() {
		String[] keywordNames = new String[0];
		mockJavaLibrary.expects(once()).method("getKeywordNames")
			.will(returnValue(keywordNames));
		
		assertEquals(keywordNames, robotRmiService.getKeywordNames());
	}
	
	public void testUsesRobotJavaLibraryForRunningKeyword() {
		mockJavaLibrary.expects(once()).method("runKeyword")
			.with(same(keywordName), same(keywordArguments));

		robotRmiService.runKeyword(keywordName, keywordArguments);
	}
	
	public void testKeywordExecutionResultsWrapKeywordReturnValue() {
		mockJavaLibrary.stubs().method("runKeyword")
			.will(returnValue(keywordReturnValue));
			
		KeywordExecutionResult keywordExecutionResult = 
			robotRmiService.runKeyword(keywordName, keywordArguments);
		
		assertEquals(keywordReturnValue, keywordExecutionResult.getResult());
	}
	
	public void testStdOutLogIsPreservedIfKeywordThrowsException() {
		KeywordExecutionResult keywordExecutionResults = 
			executeMockKeyword(ExceptionThrowingKeyword.KEYWORD_NAME);
		
		assertEquals(ExceptionThrowingKeyword.LOG_STRING_STDOUT, keywordExecutionResults.getStdOutAsString());
	}
	
	public void testStdErrLogIsPreservedIfKeywordThrowsException() {
		KeywordExecutionResult keywordExecutionResults = 
			executeMockKeyword(ExceptionThrowingKeyword.KEYWORD_NAME);
		
		assertEquals(ExceptionThrowingKeyword.LOG_STRING_STDERR, keywordExecutionResults.getStdErrAsString());
	}
	
	public void testKeywordExecutionStatusIsPassedIfKeywordPasses() {
		mockJavaLibrary.stubs().method("runKeyword")
			.will(returnValue(new Object()));
		
		assertTrue(robotRmiService.runKeyword(keywordName, keywordArguments).isKeywordPassed());
	}
	
	public void testKeywordExecutionStatusIsFailedIfTestFails() {
		mockJavaLibrary.stubs().method("runKeyword")
			.will(throwException(new Throwable()));
		
		assertFalse(robotRmiService.runKeyword(keywordName, keywordArguments).isKeywordPassed());
	}
	
	public void testGetTestFailedExceptionReturnsNullIfKeywordPassed() {
		KeywordExecutionResult executionResult = robotRmiService.runKeyword(keywordName, keywordArguments);
		assertTrue(executionResult.getTestFailedException() == null);
	}
	
	public void testGetTestFailedExceptionReturnsNotNullIfTestFailed() {
		mockJavaLibrary.stubs().method("runKeyword")
			.will(throwException(new RuntimeException()));
		
		KeywordExecutionResult executionResult = robotRmiService.runKeyword(keywordName, keywordArguments);
		assertNotNull(executionResult.getTestFailedException());
	}
	
	public void testExceptionsThrownByKeywordsAreCaught() {
		mockJavaLibrary.stubs().method("runKeyword")
			.will(throwException(new RuntimeException()));
		
		robotRmiService.runKeyword(keywordName, keywordArguments);
	}
	
	public void testKeywordExecutionResultsContainStdOutAsString() {
		KeywordExecutionResult keywordExecutionResults = executeMockKeyword(LoggingKeyword.KEYWORD_NAME);
		assertEquals(LoggingKeyword.LOG_STRING_STDOUT, keywordExecutionResults.getStdOutAsString());
	}
	
	public void testKeywordExecutionResultsContainStdErrAsString() {
		KeywordExecutionResult keywordExecutionResults = executeMockKeyword(LoggingKeyword.KEYWORD_NAME);
		assertEquals(LoggingKeyword.LOG_STRING_STDERR, keywordExecutionResults.getStdErrAsString());
	}
	
	public void testStdStreamsAreResetAfterKeywordIsRun() {
		mockJavaLibrary.expects(once()).method("runKeyword").id("runKwd");
		mockStreamRedirecter.expects(once()).method("resetStdStreams").after(mockJavaLibrary, "runKwd");
		
		robotRmiService.runKeyword(keywordName, keywordArguments);
	}

	public void testStdStreamsAreResetIfKeywordThrowsException() {
		mockJavaLibrary.expects(once()).method("runKeyword")
			.will(throwException(new RuntimeException()));
		mockStreamRedirecter.expects(once()).method("resetStdStreams");

		robotRmiService.runKeyword(keywordName, keywordArguments);
	}
	
	public void testExceptionThrownInKeywordIsWrappedInTestFailedException() {
		mockJavaLibrary.expects(once()).method("runKeyword")
			.will(throwException(new MockException()));
		
		KeywordExecutionResult executionResult = robotRmiService.runKeyword(keywordName, keywordArguments);
		assertEquals(MockException.class.getName(), executionResult.getTestFailedException().getSourceExceptionClassName());
	}
	
	public void testSetsTheGivenPropertiesToJavaLibraryWithPropertyAccessor() {
		String somePropertyName = "someProperty";
		String somePropertyValue = "someValue";
		String anotherPropertyName = "anotherProperty";
		String anotherPropertyValue = "anotherValue";
		
		RobotRmiService rmiService = 
			getRmiServiceWithPropertyAccessorExpectingThesePropertyValues(
					new PropertyValue(somePropertyName, somePropertyValue),
					new PropertyValue(anotherPropertyName, anotherPropertyValue));
		
		String propertyPattern = somePropertyName + "=" + somePropertyValue + "|" +
							     anotherPropertyName + "=" + anotherPropertyValue;
		
		rmiService.setLibraryProperties(propertyPattern);
	}

	private RobotRmiService getRmiServiceWithPropertyAccessorExpectingThesePropertyValues(PropertyValue someProperty, PropertyValue anotherProperty) {
		Mock mockPropertyAccessor = mock(PropertyAccessor.class);
		
		mockPropertyAccessor.expects(once()).method("setPropertyValue")
			.with(eq(someProperty));
		mockPropertyAccessor.expects(once()).method("setPropertyValue")
			.with(eq(anotherProperty));
		
		final PropertyAccessor propertyAccessor = (PropertyAccessor) mockPropertyAccessor.proxy();
		
		RobotRmiService rmiService = new SimpleRobotRmiService() {
			protected PropertyAccessor getLibraryPropertyAccessor() {
				return propertyAccessor;
			}
		};
		
		return rmiService;
	}
	
	private KeywordExecutionResult executeMockKeyword(String mockKeywordName) {
		SimpleRobotRmiService tmpRmiService = new SimpleRobotRmiService();
		tmpRmiService.setLibrary(new MockJavaLibrary());
		return tmpRmiService.runKeyword(mockKeywordName, null);
	}
	
	private void setRobotRmiService() {
		robotRmiService = new SimpleRobotRmiService((StdStreamRedirecter)mockStreamRedirecter.proxy());
		robotRmiService.setLibrary((RobotJavaLibrary) mockJavaLibrary.proxy());
	}
	
	private void setMockStreamRedirecter() {
		mockStreamRedirecter = mock(StdStreamRedirecter.class);
		mockStreamRedirecter.stubs();
	}
	
	private void setMockJavaLibrary() {
		mockJavaLibrary = mock(RobotJavaLibrary.class);
		mockJavaLibrary.stubs();
	}
}
