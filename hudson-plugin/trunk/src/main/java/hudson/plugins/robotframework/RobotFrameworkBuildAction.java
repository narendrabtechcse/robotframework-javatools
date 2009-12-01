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
	private String reportHtml;
    private String logHtml;
	private static final String REPORT_FILE_NAME = "report.html";
	
	public RobotFrameworkBuildAction(AbstractBuild<?, ?> build) {
	    System.out.println("RobotFrameworkBuildAction-contructor()!!!");
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
	    if (reportHtml == null)
	        reportHtml = parseHtml(REPORT_FILE_NAME);
	    return reportHtml;
	}

	private String html;
	public String getHtml() throws MalformedURLException, IOException {
	    if (isDynamic) {
	        isDynamic = false; // reset flag
	        html = logHtml;
	    } else {
            html = parseHtml(REPORT_FILE_NAME);
	    }
	    return html;
	}

	private String parseHtml(String reportFileName) throws MalformedURLException, IOException {
        String buildDirPath = build.getRootDir()
		                           .getPath();
        String htmlReportPath = buildDirPath+File.separator+reportFileName;
		String html = new RobotFrameworkHtmlParser().parseFrom(htmlReportPath);
        return html;
    }
	
	private boolean isDynamic;
	public void doDynamic(StaplerRequest req, StaplerResponse resp) throws ServletException, IOException {
	    isDynamic = true;
        String fileName = req.getRestOfPath()
                             .replaceFirst("/", "");
        if (logHtml == null)
            logHtml = parseHtml(fileName);
	    resp.forwardToPreviousPage(req);
	}
	
}