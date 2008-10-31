package org.robotframework.maven.util;

import java.io.IOException;
import java.io.OutputStream;

public class DuplicatingOutputStream extends OutputStream {

    private final OutputStream stream1;

    private final OutputStream stream2;

    public DuplicatingOutputStream(OutputStream stream1,
            OutputStream stream2) {
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    public void write(int b) throws IOException {
        stream1.write(b);
        stream2.write(b);
    }
}
