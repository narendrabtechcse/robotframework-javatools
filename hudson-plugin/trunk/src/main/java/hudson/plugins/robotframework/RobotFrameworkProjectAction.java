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

import hudson.FilePath;
import hudson.model.Project;

public class RobotFrameworkProjectAction extends RobotFrameworkAction {

    private String testExecutionsResultPath;
    private Project<?, ?> project;

    public RobotFrameworkProjectAction(Project<?, ?> project, String testExecutionResultPath) {
    	this.project = project;
        this.testExecutionsResultPath = testExecutionResultPath;
    }

    public Project<?, ?> getProject() {
        return project;
    }
    
    @Override
    protected FilePath getReportRootDir() {
        FilePath workspace = project.getWorkspace();
        if (isNotNullOrBlank(testExecutionsResultPath))
            return new FilePath(workspace, testExecutionsResultPath);
        return workspace;
    }
}
