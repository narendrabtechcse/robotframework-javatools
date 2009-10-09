package robotframework;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import org.kohsuke.stapler.DataBoundConstructor;

public class RobotFrameworkResultPublisher extends Recorder {

	private final String testExecutionResultPath;

	@DataBoundConstructor
	public RobotFrameworkResultPublisher(String path) {
		this.testExecutionResultPath = path;
	}

	public String getPath() {
		return testExecutionResultPath;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		build.addAction(new ResultPublishingAction());
		return true;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		public String getDisplayName() {
			return "Publish Robot Framework test results";
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
