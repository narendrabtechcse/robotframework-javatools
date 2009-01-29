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
import org.robotframework.javalib.util.StdStreamRedirecter;
import org.robotframework.jvmconnector.common.TestFailedException;
import org.robotframework.jvmconnector.server.KeywordExecutionResultImpl;


public class KeywordExecutionResultImplTest extends MockObjectTestCase {
	private KeywordExecutionResultImpl executionResultImpl;

	public void setUp() {
		executionResultImpl = new KeywordExecutionResultImpl();
	}
	
	public void testGetsStandardStreamsFromStdStreamRedirecter() {
		Mock mockStdStreamRedirecter = mock(StdStreamRedirecter.class);
		mockStdStreamRedirecter.expects(once()).method("getStdOutAsString");
		mockStdStreamRedirecter.expects(once()).method("getStdErrAsString");
		
		executionResultImpl.setStdStreams((StdStreamRedirecter) mockStdStreamRedirecter.proxy());
	}
	
	public void testStatusIsPassed() {
		assertTrue(executionResultImpl.keywordPassed());
	}
	
	public void testSettingKeywordFaileExceptionSetsTheStatusToFailed() {
		executionResultImpl.setTestFailedException(new TestFailedException(new Exception()));
		assertFalse(executionResultImpl.keywordPassed());
	}
}
