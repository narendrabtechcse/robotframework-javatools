package org.robotframework.jvmconnector.server;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;


public class RmiServiceAgent {
		
	public static void premain(String agentArguments, Instrumentation inst) {
		System.out.println("Starting Agent " + agentArguments);
		if (agentArguments != null) {
			String[] paths = agentArguments.split(System.getProperty("path.separator"));
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
		new RmiService().start(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "launcher.txt");

		System.out.println("Agent started");
	}	
}
