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

import hudson.model.Project;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class RobotFrameworkProjectAction extends RobotFrameworkAction {

    private String testExecutionsResultPath;
    private Project project;

    public RobotFrameworkProjectAction(Project project, String testExecutionResultPath) {
    	this.project = project;
        this.testExecutionsResultPath = testExecutionResultPath;
    }

    public Project getProject() {
        return project;
    }
    
    public void doDynamic(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        // FIXME: testExecutionsResultPath should be included in the path, now works
        // only if reports are in the workspa
        forwardToReport(req, resp, project.getWorkspace());
    }
}
