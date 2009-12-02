package hudson.plugins.robotframework;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;

public class RobotFrameworkAction implements Action {

    private static final String REPORT_INDEX_HTML = "report.html";
    
    public String getDisplayName() {
        return RobotFrameworkPlugin.DISPLAY_NAME;
    }

    public String getIconFileName() { 
        return RobotFrameworkPlugin.ICON_FILE_NAME;
    }

    public String getUrlName() {
        return RobotFrameworkPlugin.URL_NAME;
    }

    protected void forwardToReport(StaplerRequest req, StaplerResponse resp, FilePath reportRootDir) throws IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, 
                                                                  reportRootDir, 
                                                                  getDisplayName(), 
                                                                  "folder.gif", 
                                                                  false);
        dbs.setIndexFileName(REPORT_INDEX_HTML);
        dbs.generateResponse(req, resp, this);
    }
}
