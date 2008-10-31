package org.robotframework.maven.platform;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robotframework.maven.platform.Execution;
import org.robotframework.maven.platform.Platform;

public class FakePlatform extends Platform {

    private Map systemEnvironment = new HashMap();

    public final String scriptPrefix = "prefix";

    public List executedCommand;

    protected Execution printEnvironmentVariables() {
        return new Execution(Collections.singletonList("set"));
    }

    protected void addScriptPrefix(List command) {
        command.add(scriptPrefix);
    }

    public String getEnvironmentVariable(String name) {
        return (String) systemEnvironment.get(name);
    }

    public String getName() {
        return "TestDouble";
    }

    public void setupEnvironmentVariable(String name, String value) {
        systemEnvironment.put(name, value);
    }

    protected Execution createExecution(List command) {
        executedCommand = command;
        return new FakeExecution();
    }
}
