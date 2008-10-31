package org.robotframework.maven.testutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class InstallationFinder {

    protected final List locations = new ArrayList();

    protected final List suffixes = new ArrayList();

    public InstallationFinder() {
        suffixes.add("");
    }

    protected File find(String baseName) {
        for (Iterator i = locations.iterator(); i.hasNext();) {
            String location = (String) i.next();
            for (Iterator j = suffixes.iterator(); j.hasNext();) {
                File file = new File(location, baseName + j.next());
                if (file.exists() && !file.isDirectory()) {
                    return file;
                }
            }
        }
        throw new RuntimeException(
                "Installation not found from the usual locations: "
                        + locations);
    }

}
