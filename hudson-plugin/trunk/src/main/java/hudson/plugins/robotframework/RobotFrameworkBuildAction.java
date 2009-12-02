package hudson.plugins.robotframework;

import hudson.FilePath;
import hudson.model.AbstractBuild;


public class RobotFrameworkBuildAction extends RobotFrameworkAction {

    private String testExecutionsResultPath;
	private AbstractBuild<?, ?> build;
	
	public RobotFrameworkBuildAction(AbstractBuild<?, ?> build, String testExecutionResultPath) {
		this.build = build;
		this.testExecutionsResultPath = testExecutionResultPath;
		copyRobotFilesToBuildDir(build, testExecutionsResultPath);
	}
	
	private void copyRobotFilesToBuildDir(AbstractBuild<?, ?> build, String testExecutionsResultPath) {
		try {
			FilePath destDir = getRobotReportsDir(build, testExecutionsResultPath);
            build.getWorkspace().copyRecursiveTo("*.*ml", destDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    private FilePath getRobotReportsDir(AbstractBuild<?, ?> build, String testExecutionsResultPath) {
        if (isNotNullOrBlank(testExecutionsResultPath))
            return new FilePath(new FilePath(build.getRootDir()), testExecutionsResultPath);
        return new FilePath(build.getRootDir());
    }

	public AbstractBuild<?, ?> getBuild() {
		return build;
    }

    @Override
    protected FilePath getReportRootDir() {
        return getRobotReportsDir(build, testExecutionsResultPath);
    }	
}