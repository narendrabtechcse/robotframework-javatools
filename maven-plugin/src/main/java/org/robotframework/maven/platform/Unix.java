/* Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robotframework.maven.platform;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lasse Koskela
 */
public class Unix extends Platform {

    public String getName() {
        return "Unix/Linux";
    }

    protected Execution printEnvironmentVariables()
            throws IOException {
        return runExecutable("env");
    }

    public void addScriptPrefix(List command) {
        command.add("sh");
        command.add("-c");
    }

    protected void addScriptAndArguments(List command, String file,
            List args) {
        StringBuffer s = new StringBuffer();
        s.append(file);
        for (Iterator i = args.iterator(); i.hasNext();) {
            s.append(" ");
            s.append(quote(String.valueOf(i.next())));
        }
        command.add(s.toString());
    }

    private String quote(String argument) {
        if (argument.startsWith("-")) {
            return argument;
        }
        StringBuffer s = new StringBuffer(argument.length() + 10);
        s.append("\"");
        s.append(argument.replaceAll("\"", "\\\""));
        s.append("\"");
        return s.toString();
    }
}
