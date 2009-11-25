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


public class RobotFrameworkBuildAction extends RobotframeworkAction {

	private AbstractBuild<?, ?> build;
	
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
		return build.getUrl()+"report.html";
	}
	
	public String getHtmlReport() throws IOException {
		String buildDirPath = build.getRootDir()
		                           .getPath();
		String htmlReportPath = buildDirPath+File.separator+"report.html";
		return getHtml(htmlReportPath);
	}

	private String getHtml(String htmlReportPath) throws MalformedURLException, IOException {
	    StreamedSource source = new StreamedSource(new BufferedReader(new FileReader(htmlReportPath)));
	    StringBuilder html = new StringBuilder();
	    boolean inBody = false;
	    for (Segment segment: source) {
	       if (segment instanceof Tag) {
  	           Tag tag = (Tag)segment;
  	           if (tag.getName().equalsIgnoreCase("body"))
  	               inBody = !inBody;
	       }
	       if (inBody)
	           html.append(segment.toString());
	    }
	    return html.toString();
	}
}