/*
 * Copyright 2009 Nokia Siemens Networks Oyj
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


package org.robotframework.jvmconnector.agent;

import java.util.ArrayList;
import java.util.List;

public class AgentConfiguration {

    private Integer port;
    private List<String> jars = new ArrayList<String>();

    public AgentConfiguration(String arguments) {
        parse(arguments);
    }

    private void parse(String arguments) {
    	String driveLetter = "";
    	for (String item : arguments.split(":")) {
        	if (item.length() == 1) {
        		driveLetter = item;
        	    continue;
        	}
        	if (driveLetter.length() == 1) {
        		item = driveLetter + ":" + item;
        	}
    		driveLetter = "";
            if (item.toLowerCase().contains("port"))
                port = Integer.valueOf(item.substring(5));
            else
                jars.add(item);
        }
        
    }

    public Integer getPort() {
        return port;
    }

    public List<String> getJars() {
        return jars;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jars == null) ? 0 : jars.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AgentConfiguration other = (AgentConfiguration) obj;
        if (jars == null) {
            if (other.jars != null)
                return false;
        } else if (!jars.equals(other.jars))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        return true;
    }

    
}
