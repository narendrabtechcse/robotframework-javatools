package hudson.plugins.robotframework;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;


public class RobotFrameworkBuildAction extends RobotFrameworkAction {

	private AbstractBuild<?, ?> build;
	private static final String REPORT_FILE_NAME = "report.html";
	
	public RobotFrameworkBuildAction(AbstractBuild<?, ?> build) {
		this.build = build;
		copyRobotFilesToBuildDir(build);
	}
	
	private void copyRobotFilesToBuildDir(AbstractBuild<?, ?> build) {
		try {
			build.getWorkspace().copyRecursiveTo("*.*ml", new FilePath(build.getRootDir()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AbstractBuild<?, ?> getBuild() {
		return build;
    }
	
	public String getReportFilePath() {
		return build.getUrl()+REPORT_FILE_NAME;
	}
	
	public String getHtmlReport() throws IOException {
		String buildDirPath = build.getRootDir()
		                           .getPath();
        String htmlReportPath = buildDirPath+File.separator+REPORT_FILE_NAME;
		return new RobotFrameworkHtmlParser().parseFrom(htmlReportPath);
	}
}