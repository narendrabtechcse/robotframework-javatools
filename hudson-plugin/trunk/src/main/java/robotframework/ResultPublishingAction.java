package robotframework;

import hudson.model.Action;

public class ResultPublishingAction implements Action {

	public String getDisplayName() {
		return "Robot Framework test execution results";
	}

	public String getIconFileName() {
		return "/rf-hudson/webapp/robot.png";
	}

	public String getUrlName() {
		return "http://robotframework.org";
	}

}
