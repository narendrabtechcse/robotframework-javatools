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
import java.net.URL;

import org.robotframework.maven.util.Path;

import junit.framework.TestCase;

/**
 * @author Lasse Koskela
 */
public class TestPath extends TestCase {

    public void testFile() throws Exception {
        File file = new File("/tmp/foo.txt");
        assertEquals(file.getAbsolutePath(), Path.to(file));
    }

    public void testFileURL() throws Exception {
        assertEquals("/tmp/foo.txt", Path.to(new URL("file:"
                + "/tmp/foo.txt")));
        assertEquals("C:/tmp/foo.txt", Path.to(new URL("file:"
                + "C:/tmp/foo.txt")));
    }

    public void testJarFileURL() throws Exception {
        assertEquals("/tmp/foo.txt", Path.to(new URL("jar:file:"
                + "/tmp/foo.txt!/")));
        assertEquals("c:/tmp/foo.txt", Path.to(new URL("jar:file:"
                + "c:/tmp/foo.txt!/")));
    }
}
