package org.robotframework.maven.platform;

import java.util.Collections;

import org.robotframework.maven.platform.Execution;

public class FakeExecution extends Execution {

    public FakeExecution() {
        super(Collections.singletonList("whatever"));
    }

    public void run() {
    }
}
