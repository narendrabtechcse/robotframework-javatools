package hudson.plugins.robotframework;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


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
	
	public InputStream getXmlReportInputStream() throws IOException {
		return new FilePath(getXmlReportFile()).read();
	}

	public String getXmlReport() throws IOException {
		String xml = new FilePath(getXmlReportFile()).readToString();
		return xml;
	}
	
	private File getXmlReportFile() {
		String buildDirPath = build.getRootDir()
		                           .getPath();
		File outputXmlFile = new File(buildDirPath+File.separator+"output.xml");
		return outputXmlFile;
	}
	
	public String getHtmlReport() throws IOException {
		String buildDirPath = build.getRootDir()
		                           .getPath();
		String htmlReportPath = buildDirPath+File.separator+"report.html";
		String html = parseHtml(htmlReportPath);
		return html;
	}
	
	private String parseHtml(String htmlReportPath) {	
		try {
			boolean inScript = false;
			boolean inStyle = false;
			boolean inBody = false;
			StringBuilder sb = new StringBuilder();
		    BufferedReader br = new BufferedReader(new FileReader(htmlReportPath));
		    String line;
		    while ((line=br.readLine()) != null) {
		    	if (line.startsWith("<script>")) {
		    		inScript = true;
		    	}
		    	if (inScript) {
		    		sb.append(line);
		    	}
		    	if (line.startsWith("</script>")) {
		    		inScript = false;
		    	}
		    	
		    	if (line.startsWith("<style")) {
		    		inStyle = true;
		    	}
		    	if (inStyle) {
		    		parseStyles(line, sb);
		    	}
		    	if (line.startsWith("</style>")) {
		    		inStyle = false;
		    	}
		    	
		    	if (line.startsWith("<body")) {
		    		inBody = true;
		    		continue;
		    	}
		    	if (line.startsWith("</body>"))
		    		inBody = false;
		    	if (inBody)
		    		sb.append(line);
		    }
		    return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	    
	}

	private void parseStyles(String line, StringBuilder html) {
//		html.append(line);		
	}
}