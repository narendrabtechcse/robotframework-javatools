package org.robotframework.maven.platform;

import java.io.IOException;
import java.util.List;

/**
 * @author Lasse Koskela
 */
public class Windows extends Platform {

    public String getName() {
        return "Windows";
    }

    protected Execution printEnvironmentVariables()
            throws IOException {
        return runScript("set");
    }

    public void addScriptPrefix(List command) {
        command.add("cmd.exe");
        command.add("/C");
    }
}
