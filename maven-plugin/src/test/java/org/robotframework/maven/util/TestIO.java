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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.robotframework.maven.util.IO;

import junit.framework.TestCase;

/**
 * @author Lasse Koskela
 */
public class TestIO extends TestCase {

    private PrintStream stdout;

    private PrintStream stderr;

    protected void setUp() throws Exception {
        super.setUp();
        stdout = System.out;
        stderr = System.err;
        System.setOut(new PrintStream(new NullOutputStream()));
        System.setErr(new PrintStream(new NullOutputStream()));
    }

    protected void tearDown() throws Exception {
        System.setOut(stdout);
        System.setErr(stderr);
        super.tearDown();
    }

    public void testConsumingAnInputStream() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(
                "this is data".getBytes());
        assertTrue(inputStream.available() > 0);
        IO.consume(inputStream).join();
        assertEquals(0, inputStream.available());
    }

    public void testCopyConsumingAnInputStream() throws Exception {
        String data = "this is data";
        InputStream from = new ByteArrayInputStream(data.getBytes());
        ByteArrayOutputStream to = new ByteArrayOutputStream();
        IO.consume(from, to).join();
        assertEquals(0, from.available());
        assertEquals(data, new String(to.toByteArray()));
    }

    public void testConsumeSwallowsIOExceptions() throws Exception {
        IO.consume(new InputStream() {
            public int read() throws IOException {
                throw new IOException("INTENTIONAL");
            }
        }).join();
        // shouldn't have thrown an exception
    }

    public void testCopyConsumeSwallowsIOExceptionsForReads()
            throws Exception {
        IO.consume(new InputStream() {
            public int read() throws IOException {
                throw new IOException("INTENTIONAL");
            }
        }, new ByteArrayOutputStream()).join();
        // shouldn't have thrown an exception
    }

    public void testCopyConsumeSwallowsIOExceptionsForWrites()
            throws Exception {
        IO.consume(new ByteArrayInputStream("abc".getBytes()),
                new OutputStream() {
                    public void write(int b) throws IOException {
                        throw new IOException("INTENTIONAL");
                    }
                }).join();
        // shouldn't have thrown an exception
    }

    public void testDeletingFile() throws Exception {
        File f = File.createTempFile("foo", ".txt");
        f.createNewFile();
        assertTrue(f.exists());
        IO.delete(f);
        assertFalse(f.exists());
    }
}
