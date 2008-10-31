package org.robotframework.maven.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Lasse Koskela
 */
public abstract class IO {

    private static final int READ_BUFFER_SIZE = 8096;

    public static void delete(File file) {
        file.delete();
    }

    public static Thread consume(final InputStream from) {
        return consume(from, new OutputStream() {
            public void write(int ignored) throws IOException {
                // to /dev/null
            }
        });
    }

    public static Thread consume(final InputStream from,
            final OutputStream to) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    copy(from, to);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return thread;
    }

    public static void copy(InputStream from, OutputStream to)
            throws IOException {
        int r = -1;
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        while ((r = from.read(buffer, 0, buffer.length)) != -1) {
            to.write(buffer, 0, r);
        }
        to.flush();
    }

    public static String readToString(InputStream stream)
            throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(
                READ_BUFFER_SIZE);
        copy(stream, buffer);
        return new String(buffer.toByteArray());
    }

    public static String readToString(File file) throws IOException {
        return readToString(new FileInputStream(file));
    }
}
