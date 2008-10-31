package org.robotframework.maven.testutils;

/**
 * @author Lasse Koskela
 */
public class MavenInstallation extends InstallationFinder {

    public MavenInstallation() {
        locations.add("/usr/bin");
        locations.add("/usr/local/bin");
        locations.add("/usr/local/maven/bin");
        locations.add("/usr/local/maven2/bin");
        locations.add("/opt/maven/bin");
        locations.add("/opt/maven2/bin");
        locations.add("C:/maven/bin");
        locations.add("C:/maven2/bin");
        locations.add("C:/m2/bin");
        suffixes.add(".sh");
        suffixes.add(".bat");
    }

    public static String pathToMvn() {
        return new MavenInstallation().find("mvn").getAbsolutePath();
    }
}
