package hudson.plugins.robotframework;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


public class RobotFrameworkBuildAction extends RobotFrameworkAction {

	private AbstractBuild<?, ?> build;
    private String logHtml;
    private String reportHtml;
    private boolean logFileRequested;
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

	public String getHtml() throws MalformedURLException, IOException {
	    if (logFileRequested) {
	        logFileRequested = false; // reset flag
	        return logHtml;
	    }
	    if (reportHtml == null)
	        reportHtml = parseHtml(REPORT_FILE_NAME);
	    return reportHtml;
	}

	private String parseHtml(String reportFileName) throws MalformedURLException, IOException {
        String buildDirPath = build.getRootDir()
		                           .getPath();
        String htmlReportPath = buildDirPath+File.separator+reportFileName;
		String html = new RobotFrameworkHtmlParser().parseFrom(htmlReportPath);
        return html;
    }
	
	public void doDynamic(StaplerRequest req, StaplerResponse resp) throws ServletException, IOException {
	    logFileRequested = true;
        String fileName = req.getRestOfPath()
                             .replaceFirst("/", "");
        if (logHtml == null)
            logHtml = parseHtml(fileName);
	    resp.forwardToPreviousPage(req);
	}
	
}