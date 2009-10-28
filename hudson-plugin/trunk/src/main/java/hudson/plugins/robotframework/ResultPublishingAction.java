package hudson.plugins.robotframework;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;

public class ResultPublishingAction implements HealthReportingAction {

	private AbstractBuild build;
	private String testExecutionsResultPath;

	public ResultPublishingAction(AbstractBuild b, String path) {
		build = b;
		testExecutionsResultPath = path;
	}

	public String getDisplayName() {
		return RobotframeworkPlugin.DISPLAY_NAME;
	}

	public String getIconFileName() {
		return RobotframeworkPlugin.ICON_FILE_NAME;
	}

	public String getUrlName() {
		return "../ws/" + testExecutionsResultPath;
	}

	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}
}
