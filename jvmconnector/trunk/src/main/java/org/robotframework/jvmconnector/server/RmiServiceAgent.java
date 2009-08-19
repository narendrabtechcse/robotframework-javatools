package org.robotframework.jvmconnector.server;

import java.lang.instrument.Instrumentation;

public class RmiServiceAgent {

	public static void premain(String agentArguments) {
		new RmiService().start("/tmp/launcher.txt");
	}
	
	public static void premain(String agentArguments, Instrumentation inst) {
		premain(agentArguments);
	}
	
}
