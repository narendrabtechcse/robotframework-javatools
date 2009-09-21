/*
z * Copyright 2008 Nokia Siemens Networks Oyj
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


package org.robotframework.jvmconnector.xmlrpc;

import java.io.File;

import org.robotframework.javalib.library.RobotJavaLibrary;

public class ExampleLibrary implements RobotJavaLibrary {

    public Integer countItemsInDirectory(String path) {
        return new File(path).listFiles().length;
    }

    public void stringsShouldBeEqual(String str1, String str2) {
        System.out.println("Comparing '"+str1+"' to '"+str2+"'");
        if (!str1.equals(str2)) {
            throw new RuntimeException( "Given strings are not equal" );
        }
    }

    public String[] getKeywordNames() {
        return new String[] {"countitemsindirectory", "stringsshouldbeequal"};
    }

    public static void main(String[] args) throws Exception {
        RobotXmlRpcServer server = new RobotXmlRpcServer(new ExampleLibrary(), 8270);
        server.startServer();
    }

    public Object runKeyword(String keywordName, Object[] args) {
        if ("countitemsindirectory".equals(keywordName)) {
            return countItemsInDirectory( (String)args[0] );
        } else if("stringsshouldbeequal".equals(keywordName)) {
            stringsShouldBeEqual((String)args[0], (String)args[1]);
            return null;
        } else {
            throw new RuntimeException("Unknown keyword: " + keywordName);
        }
    }
}
