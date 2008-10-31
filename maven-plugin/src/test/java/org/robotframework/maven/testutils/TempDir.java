package org.robotframework.maven.testutils;

import java.io.File;
import java.io.IOException;

public class TempDir {

    private static final File SYSTEM_TEMP_DIR = new File(System
            .getProperty("java.io.tmpdir"));

    public static File create() {
        return create("temp");
    }

    public static File create(File parent) {
        return create("temp", parent);
    }

    public static File create(String prefix) {
        return create(prefix, SYSTEM_TEMP_DIR);
    }

    public static File create(String prefix, File parent) {
        try {
            File tempFile = File.createTempFile(prefix, "", parent);
            if (!tempFile.delete())
                throw new IOException(
                        "Failed to delete a temp file: "
                                + tempFile.getAbsolutePath());
            if (!tempFile.mkdirs())
                throw new IOException("Failed to create a temp dir: "
                        + tempFile.getAbsolutePath());
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
