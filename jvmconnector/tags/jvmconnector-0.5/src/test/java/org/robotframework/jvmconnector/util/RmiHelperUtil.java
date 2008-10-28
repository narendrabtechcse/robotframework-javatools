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

package org.robotframework.jvmconnector.util;

import java.io.IOException;
import java.net.ServerSocket;

import org.robotframework.jvmconnector.client.RobotRmiClient;
import org.robotframework.jvmconnector.common.PropertyOverrider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;


public class RmiHelperUtil {
	private static final String CLIENT_CONTEXT_XML = "org/robotframework/jvmconnector/client/clientContext.xml";
	private static final String SERVER_CONTEXT_XML = "org/robotframework/jvmconnector/server/testServerContext.xml";
	
	public static int getFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try { socket.close(); } catch (Exception e) { /* Ignore intentionally */ }
		}
	}
	
	public static GenericApplicationContext getClientBeanFactory(int port) {
		DefaultListableBeanFactory beanFactory = getBeanFactory(CLIENT_CONTEXT_XML);
		changeBeanProperty(beanFactory, "robotRmiService.serviceUrl", "rmi://localhost:" + port + "/jvmConnector");
		return new GenericApplicationContext(beanFactory);
	}

	public static GenericApplicationContext getServerApplicationContext(int port) {
		DefaultListableBeanFactory beanFactory = getBeanFactory(SERVER_CONTEXT_XML);
		changeBeanProperty(beanFactory, "serviceExporter.registryPort", "" + port);
		return new GenericApplicationContext(beanFactory);
	}

	private static DefaultListableBeanFactory getBeanFactory(String resourcePath) {
		return new XmlBeanFactory(new ClassPathResource(resourcePath));
	}

	private static void changeBeanProperty(ConfigurableListableBeanFactory beanFactory, String beanPropertyName,
		String propertyValue) {
		PropertyOverrider propertyOverrider = new PropertyOverrider();
		propertyOverrider.addOverridableProperty(beanPropertyName, propertyValue);
		propertyOverrider.postProcessBeanFactory(beanFactory);
	}
	
	public static class FakeRmiClient extends RobotRmiClient {
		private StringBuffer stdOutBuffer = new StringBuffer();
		private StringBuffer stdErrBuffer = new StringBuffer();
		
		public FakeRmiClient(BeanFactory beanFactory) {
			this(beanFactory, null);
		}
		
		public FakeRmiClient(BeanFactory beanFactory, String classpathPattern) {
			super(beanFactory, classpathPattern);
		}
		
		public String getStdOutAsString() {
			return stdOutBuffer.toString();
		}
		
		public String getStdErrAsString() {
			return stdErrBuffer.toString();
		}
		
		protected void printStdOut(String stdOutAsString) {
			stdOutBuffer.append(stdOutAsString);
		}
		
		protected void printStdErr(String stdErrAsString) {
			stdErrBuffer.append(stdErrAsString);
		}
	}
}