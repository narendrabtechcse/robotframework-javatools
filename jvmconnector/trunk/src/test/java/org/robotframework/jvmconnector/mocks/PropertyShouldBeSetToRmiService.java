package org.robotframework.jvmconnector.mocks;

import org.robotframework.javalib.keyword.Keyword;

public class PropertyShouldBeSetToRmiService implements Keyword {
	public static final String KEYWORD_NAME = "propertyShouldBeSetToRmiService";
	
	public Object execute(Object[] arguments) {
		return Boolean.TRUE;
	}
}
