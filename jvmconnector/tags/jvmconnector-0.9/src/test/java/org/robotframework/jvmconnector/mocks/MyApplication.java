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

package org.robotframework.jvmconnector.mocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;



public class MyApplication {
    public static boolean isRunning = false;
    public static String[] args;
    
    public static void main(String[] args) throws Exception {
    	if (args.length == 1 && args[0].equals("log")) {
    		startLogging();
    	}
    	
        MyApplication.args = args;
        isRunning = true;
        Thread.sleep(500);
    }
    
    private static void startLogging() throws Exception {
    	final String msg = "JvmConnector is a module that enables remote keyword invocation.";
    	final String errors = "error";
    	
    	new Thread() {
    		public void run() {
    			while(true) {
    				System.out.println(msg);
    				System.err.println(errors);
    				try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}
    		}
    	}.start();
    	Thread.sleep(10000);
	}

	public static void startAnotherInstance(String applicationArgs, String jvmArgs) {
        try {
            Runtime.getRuntime().exec("java " + jvmArgs + " " + MyApplication.class.getName() + " " + applicationArgs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
