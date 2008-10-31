package org.robotframework.maven.testutils;

import junit.framework.Assert;

public class AssertableFlag extends Flag {

    public void shouldBeSet() {
        Assert.assertTrue("Flag was not set.", isSet());
    }
}
