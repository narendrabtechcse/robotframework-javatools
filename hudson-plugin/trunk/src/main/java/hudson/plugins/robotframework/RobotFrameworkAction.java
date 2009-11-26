package hudson.plugins.robotframework;

import hudson.model.Action;

public class RobotFrameworkAction implements Action {

    public String getDisplayName() {
        return RobotFrameworkPlugin.DISPLAY_NAME;
    }

    public String getIconFileName() { 
        return RobotFrameworkPlugin.ICON_FILE_NAME;
    }

    public String getUrlName() {
        return RobotFrameworkPlugin.URL_NAME;
    }
}
