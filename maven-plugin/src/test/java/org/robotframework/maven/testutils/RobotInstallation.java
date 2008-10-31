package org.robotframework.maven.testutils;

/**
 * @author Lasse Koskela
 */
public class RobotInstallation extends InstallationFinder {

    public RobotInstallation() {
        locations.add("/usr/bin");
        locations.add("/usr/local/bin");
        locations.add("C:/Python24/Scripts");
        locations.add("C:/Python25/Scripts");
        locations
                .add("/opt/local/Library/Frameworks/Python.framework/Versions/2.4/bin");
        suffixes.add(".sh");
        suffixes.add(".bat");
    }

    public static String pathToJybot() {
        return new RobotInstallation().find("jybot")
                .getAbsolutePath();
    }

    public static String pathToPybot() {
        return new RobotInstallation().find("pybot")
                .getAbsolutePath();
    }
}
