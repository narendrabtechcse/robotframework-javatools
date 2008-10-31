package org.robotframework.maven.mocks;

import java.io.File;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author Lasse Koskela
 */
public class FakeArtifact extends DefaultArtifact {

    private final File file;

    public FakeArtifact(String file) {
        this(new File(file));
    }

    public FakeArtifact(File file) {
        super("groupId", file.getName(), VersionRange
                .createFromVersion("1.0"), "compile", "jar", file
                .getName(), null);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
