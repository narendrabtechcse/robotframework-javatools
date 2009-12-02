/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hudson.plugins.robotframework;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;

public abstract class RobotFrameworkAction implements Action {

    private String testExecutionsResultPath;
    
    public RobotFrameworkAction(String testExecutionsResultPath) {
        this.testExecutionsResultPath = testExecutionsResultPath;
    }
    
    public String getDisplayName() {
        return "Robot Framework Report";
    }

    public String getIconFileName() { 
        return "/plugin/robotframework/robot.png";
    }

    public String getUrlName() {
        return "robotframework";
    }
    
    public void doDynamic(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, 
                                                                  getReportRootDir(), 
                                                                  getDisplayName(), 
                                                                  "folder.gif", 
                                                                  false);
        dbs.setIndexFileName("report.html");
        dbs.generateResponse(req, resp, this);
    }

    protected FilePath getRobotReportsDir(FilePath rootDir) {
        if (isNotNullOrBlank(testExecutionsResultPath))
            return new FilePath(rootDir, testExecutionsResultPath);
        return rootDir;
    }
    
    protected abstract FilePath getReportRootDir();

    protected boolean isNotNullOrBlank(String testExecutionsResultPath) {
        return testExecutionsResultPath != null && testExecutionsResultPath.length() > 0;
    }
}
