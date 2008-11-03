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
import org.robotframework.jvmconnector.client.RobotRemoteLibrary;
import org.robotframework.jvmconnector.common.PropertyOverrider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


public class RobotRemoteLibraryTest extends MockObjectTestCase {
	private Mock mockBeanFactory = mock(ConfigurableListableBeanFactory.class);
	private Mock mockPropertyOverrider = mock(PropertyOverrider.class);
	private Mock mockRmiClient = mock(RobotJavaLibrary.class);

	private ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) mockBeanFactory.proxy();
	private PropertyOverrider propertyOverrider = (PropertyOverrider) mockPropertyOverrider.proxy();
	private boolean createRobotLibraryClientWasCalled;
	
	private MockRemoteLibrary remoteLibraryWithMockRmiClient = new MockRemoteLibrary() {
        protected RobotJavaLibrary createRobotLibraryClient() {
		    createRobotLibraryClientWasCalled = true;
			return (RobotJavaLibrary) mockRmiClient.proxy();
		}
	};

	protected void setUp() throws Exception {
	    createRobotLibraryClientWasCalled = false;
		mockBeanFactory.stubs();
	}

	public void testOverridesBeanPropertiesIfInitializedWithHostAndPort() throws Exception {
		String host = "testhost";
		String port = "1234";
		String expectedPropertyValue = "rmi://" + host + ":" + port + "/jvmConnector";
		mockPropertyOverrider.expects(once()).method("addOverridableProperty")
		    .with(eq("robotRmiService.serviceUrl"),	eq(expectedPropertyValue));
		mockPropertyOverrider.expects(once()).method("postProcessBeanFactory")
		    .with(same(beanFactory));

		new MockRemoteLibrary(host, port);
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
		
        String[] expectedKeywordNames = new String[] { clientKeyword, RobotRemoteLibrary.RESET };
		
		assertKeywordNamesEquals(expectedKeywordNames, remoteLibraryWithMockRmiClient.getKeywordNames());
	}
	
	public void testRunningResetKeywordResetsTheLibrary() throws Exception {
	    assertLibraryIsReset("resetRobotLibraryClient");
	    assertLibraryIsReset("Re s e t R_OBOtLibr ar yCLIE NT");
    }

    private void assertLibraryIsReset(String keyword) {
        remoteLibraryWithMockRmiClient.runKeyword(keyword, null);
	    assertTrue(remoteLibraryWithMockRmiClient.reset);
	    remoteLibraryWithMockRmiClient.reset = false;
    }
	
	public void testReinitializesBeanFactoryAfterReset() throws Exception {
	    mockRmiClient = mock(RobotJavaLibrary.class);
        mockRmiClient.expects(atLeastOnce()).method("runKeyword").with(eq("someKeyword"), null);
        
        remoteLibraryWithMockRmiClient.runKeyword(RobotRemoteLibrary.RESET, null);
	    remoteLibraryWithMockRmiClient.runKeyword("someKeyword", null);
	    
	    assertThatReinitIsDoneOnceAndOnlyOnce();
    }
	
	public void testDoesntInvokeClientKeywordsAfterRunningDefaultKeywords() throws Exception {
	    mockRmiClient.expects(never()).method("runKeyword");
        Boolean retVal = (Boolean) remoteLibraryWithMockRmiClient.runKeyword(RobotRemoteLibrary.RESET, null);
        assertTrue(retVal.booleanValue());
    }
	
	private void assertThatReinitIsDoneOnceAndOnlyOnce() {
	    assertTrue(createRobotLibraryClientWasCalled);
	    createRobotLibraryClientWasCalled = false;
	    remoteLibraryWithMockRmiClient.runKeyword("someKeyword", null);
	    assertFalse(createRobotLibraryClientWasCalled);
	}

	private void assertKeywordNamesEquals(String[] expectedKeywordNames, String[] keywordNames) {
	    assertEquals(Arrays.asList(expectedKeywordNames), Arrays.asList(keywordNames));
	}

    private class MockRemoteLibrary extends RobotRemoteLibrary {
        public MockRemoteLibrary(String host, String port) {
			super(host, port);
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
