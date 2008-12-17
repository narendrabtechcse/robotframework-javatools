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

package org.robotframework.jvmconnector.client;

import java.util.Arrays;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.robotframework.javalib.library.RobotJavaLibrary;
import org.robotframework.jvmconnector.common.PropertyOverrider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


public class RobotRemoteLibraryTest extends MockObjectTestCase {
	private Mock mockBeanFactory = mock(ConfigurableListableBeanFactory.class);
	private Mock mockPropertyOverrider = mock(PropertyOverrider.class);
	private Mock mockRmiClient = mock(RobotJavaLibrary.class);

	private ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) mockBeanFactory.proxy();
	private PropertyOverrider propertyOverrider = (PropertyOverrider) mockPropertyOverrider.proxy();
	
	private MockRemoteLibrary remoteLibraryWithMockRmiClient = new MockRemoteLibrary() {
        protected RobotJavaLibrary createRobotLibraryClient() {
			return (RobotJavaLibrary) mockRmiClient.proxy();
		}
	};

	protected void setUp() throws Exception {
		mockBeanFactory.stubs();
	}

	public void testOverridesDefaultValues() throws Exception {
		String uri = "rmi://somehost:9999/someLibrary";
		mockPropertyOverrider.expects(once()).method("addOverridableProperty")
		    .with(eq("robotRmiService.serviceUrl"),	eq(uri));
		mockPropertyOverrider.expects(once()).method("postProcessBeanFactory")
		    .with(same(beanFactory));

		new MockRemoteLibrary(uri);
	}

	public void testDelegatesRunKeywordCallToRmiClient() throws Exception {
		String keywordName = "testKeyword";
		Object[] keywordArgs = new Object[] { "keywordArg" };
		Object expectedReturnValue = "keywordReturnValue";

		mockRmiClient.expects(once()).method("runKeyword").with(eq(keywordName), eq(keywordArgs)).will(
			returnValue(expectedReturnValue));

		assertEquals(expectedReturnValue, remoteLibraryWithMockRmiClient.runKeyword(keywordName, keywordArgs));
	}

	public void testReturnsDefaultKeywordNamesAndThoseProvidedByRmiClient() throws Exception {
	    String clientKeyword = "someKeyword";

	    mockRmiClient.expects(once()).method("getKeywordNames")
		    .will(returnValue(new String[] { clientKeyword }));
		
        String[] expectedKeywordNames = new String[] { clientKeyword };
		
		assertKeywordNamesEquals(expectedKeywordNames, remoteLibraryWithMockRmiClient.getKeywordNames());
	}
	
	private void assertKeywordNamesEquals(String[] expectedKeywordNames, String[] keywordNames) {
	    assertEquals(Arrays.asList(expectedKeywordNames), Arrays.asList(keywordNames));
	}

    private class MockRemoteLibrary extends RobotRemoteLibrary {
        public MockRemoteLibrary(String uri) {
            super(uri);
        }
        
		public MockRemoteLibrary() {}

		ConfigurableListableBeanFactory createBeanFactory() {
			return beanFactory;
		}

		PropertyOverrider createPropertyOverrider() {
			return propertyOverrider;
		}
	}
}
