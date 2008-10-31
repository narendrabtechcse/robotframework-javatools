/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robotframework.maven.util;

import java.io.File;
import java.util.Arrays;

import org.robotframework.maven.util.ClasspathBuilder;

import junit.framework.TestCase;

/**
 * @author Lasse Koskela
 */
public class TestClasspathBuilder extends TestCase {

    private static final String[] ELEMENTS = { "first.jar",
            "second.jar", "path/to/third.jar", "/tmp/fourth.jar" };

    private ClasspathBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        builder = new ClasspathBuilder(false);
    }

    public void testNoSystemClasspathAndNoPathElementsProducesEmptyClasspath()
            throws Exception {
        assertEquals("", builder.toString());
    }

    public void testAddingNoPathElementsProducesClasspathEqualToSystemClasspath()
            throws Exception {
        builder = new ClasspathBuilder(true);
        assertEquals(System.getProperty("java.class.path"), builder
                .toString());
    }

    public void testAddingClasspathElementsOneByOne()
            throws Exception {
        builder.add(ELEMENTS[0]);
        builder.add(ELEMENTS[2]);
        String sep = System.getProperty("path.separator");
        assertEquals(ELEMENTS[0] + sep + ELEMENTS[2], builder
                .toString());
    }

    public void testAddingMultipleClasspathElementsAsArray()
            throws Exception {
        builder.add(ELEMENTS);
        String sep = System.getProperty("path.separator");
        assertEquals(ELEMENTS[0] + sep + ELEMENTS[1] + sep
                + ELEMENTS[2] + sep + ELEMENTS[3], builder.toString());
    }

    public void testAddingMultipleClasspathElementsAsList()
            throws Exception {
        builder.add(Arrays.asList(ELEMENTS));
        String sep = System.getProperty("path.separator");
        assertEquals(ELEMENTS[0] + sep + ELEMENTS[1] + sep
                + ELEMENTS[2] + sep + ELEMENTS[3], builder.toString());
    }

    public void testAddingTheContainingDirectoryOfClassInClasspath()
            throws Exception {
        builder.addContainerOf(String.class);
        File toolsJar = new File(builder.toString());
        assertTrue("tools.jar was not found at "
                + toolsJar.getAbsolutePath(), toolsJar.exists());
    }
}
