package hudson.plugins.robotframework;

import hudson.model.Action;

public class RobotframeworkAction implements Action {

    public String getDisplayName() {
        return RobotframeworkPlugin.DISPLAY_NAME;
    }

    public String getIconFileName() { 
        return RobotframeworkPlugin.ICON_FILE_NAME;
    }

    public String getUrlName() {
        return RobotframeworkPlugin.URL_NAME;
    }
	
}
