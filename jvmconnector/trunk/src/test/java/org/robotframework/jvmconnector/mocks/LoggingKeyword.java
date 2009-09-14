package org.robotframework.jvmconnector.mocks;

import org.robotframework.javalib.keyword.Keyword;


public class LoggingKeyword implements Keyword {
	public static final String LOG_STRING_STDOUT = "mock keyword executing";
	public static final String LOG_STRING_STDERR = "some error occurred";
	public static final Object RETURN_VALUE = Boolean.TRUE;
	public static final String KEYWORD_NAME = "LoggingKeyword";

	public Object execute(Object[] arg0) {
		System.out.print(LOG_STRING_STDOUT);
		System.err.print(LOG_STRING_STDERR);
		return RETURN_VALUE;
	}
	
	public String getName() {
		return KEYWORD_NAME;
	}
}
