package org.robotframework.jvmconnector.server;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;


public class RmiServiceAgent {
		
	public static void premain(String agentArguments, Instrumentation inst) {
		System.out.println("Starting Agent");
		if (agentArguments != null) {
			String[] paths = agentArguments.split(":");
			for (String path : paths) {
				try {
					System.out.println("Appending path: " + path);

					inst.appendToSystemClassLoaderSearch(new JarFile(path));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		new RmiService().start("/tmp/launcher.txt");

		System.out.println("Agent started");
	}	
}
