package robotframework;

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
		return "Robot Framework test execution results";
	}

	public String getIconFileName() {
		return "/rf-hudson/webapp/robot.png";
	}

	public String getUrlName() {
		return "../ws" + "/" + testExecutionsResultPath;
	}

	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}
}
