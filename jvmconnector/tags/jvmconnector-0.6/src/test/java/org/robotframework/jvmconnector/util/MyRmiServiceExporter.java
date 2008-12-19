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

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * @author Heikki Hulkko
 */
public class MyRmiServiceExporter extends RmiServiceExporter {
	private Registry myReg;

	protected Registry getRegistry(int port) throws RemoteException {
		if (myReg == null) {
			myReg = super.getRegistry(port);
			return myReg;
		}
		return super.getRegistry(port);
	}
	
	public void destroy() throws RemoteException {
		super.destroy();
		UnicastRemoteObject.unexportObject(myReg, true);
	}
}
