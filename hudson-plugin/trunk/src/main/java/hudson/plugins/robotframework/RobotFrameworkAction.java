package hudson.plugins.robotframework;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;

public abstract class RobotFrameworkAction implements Action {

    public static final String ICON_FILE_NAME = "/plugin/robotframework/robot.png";
    public static final String DISPLAY_NAME = "Robot Framework Report";
    public static final String URL_NAME = "robotframework";
    
    private static final String REPORT_INDEX_HTML = "report.html";
    
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public String getIconFileName() { 
        return ICON_FILE_NAME;
    }

    public String getUrlName() {
        return URL_NAME;
    }
    
    public void doDynamic(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, 
                                                                  getReportRootDir(), 
                                                                  getDisplayName(), 
                                                                  "folder.gif", 
                                                                  false);
        dbs.setIndexFileName(REPORT_INDEX_HTML);
        dbs.generateResponse(req, resp, this);
    }

    protected abstract FilePath getReportRootDir();
}
