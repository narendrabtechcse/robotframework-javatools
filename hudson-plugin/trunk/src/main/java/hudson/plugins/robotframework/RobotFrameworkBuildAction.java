package hudson.plugins.robotframework;

import hudson.FilePath;
import hudson.model.AbstractBuild;


public class RobotFrameworkBuildAction extends RobotFrameworkAction {

	private AbstractBuild<?, ?> build;
	
	public RobotFrameworkBuildAction(AbstractBuild<?, ?> build) {
		this.build = build;
		copyRobotFilesToBuildDir(build);
	}
	
	private void copyRobotFilesToBuildDir(AbstractBuild<?, ?> build) {
		try {
		    // TODO: Should the configured testExecutionsResultPath be included?
			build.getWorkspace().copyRecursiveTo("*.*ml", new FilePath(build.getRootDir()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AbstractBuild<?, ?> getBuild() {
		return build;
    }

    @Override
    protected FilePath getReportRootDir() {
        // TODO: Should the configured testExecutionsResultPath be included?
        return new FilePath(build.getRootDir());
    }	
}