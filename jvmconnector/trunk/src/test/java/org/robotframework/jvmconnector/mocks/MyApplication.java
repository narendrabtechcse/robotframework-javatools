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

import java.io.IOException;



public class MyApplication {
    public static boolean isRunning = false;
    public static String[] args;
    
    public static void main(String[] args) throws Exception {
        MyApplication.args = args;
        isRunning = true;
        Thread.sleep(500);
    }
    
    public static void startAnotherInstance(String[] arguments) {
        try {
            Runtime.getRuntime().exec("java " + MyApplication.class.getName() + " " + toString(arguments));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toString(String[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (String arg : arguments) {
            sb.append(arg + " ");
        }
        return sb.toString();
    }
}
