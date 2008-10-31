package org.robotframework.maven.testutils;

public class Flag {

    private boolean isSet = false;

    public void set() {
        isSet = true;
    }

    public boolean isSet() {
        return isSet;
    }
}
