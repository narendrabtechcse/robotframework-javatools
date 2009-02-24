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

import org.robotframework.jvmconnector.util.RmiHelperUtil;
import org.springframework.context.support.GenericApplicationContext;


public class RmiServiceLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
    private int port = 1099;
    private GenericApplicationContext serverApplicationContext; 
	
	public RmiServiceLibrary() { }
	
	public RmiServiceLibrary(String port) {
		this.port = Integer.parseInt(port);
	}
	
	public void startRmiService() {
	    serverApplicationContext = RmiHelperUtil.getServerApplicationContext(port);
		serverApplicationContext.refresh();
	}
	
	public void closeRmiService() {
	    serverApplicationContext.close();
	}
}
