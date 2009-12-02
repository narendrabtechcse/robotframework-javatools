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
import hudson.model.AbstractBuild;


public class RobotFrameworkBuildAction extends RobotFrameworkAction {

	private AbstractBuild<?, ?> build;
	
	public RobotFrameworkBuildAction(AbstractBuild<?, ?> build, String testExecutionsResultPath) {
	    super(testExecutionsResultPath);
		this.build = build;
		copyRobotFilesToBuildDir();
	}
	
	private void copyRobotFilesToBuildDir() {
		try {
            FilePath srcDir = getRobotReportsDir(build.getWorkspace());
            FilePath destDir = getRobotReportsDir(new FilePath(build.getRootDir()));
            srcDir.copyRecursiveTo("*.*ml", destDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
    }

    @Override
    protected FilePath getReportRootDir() {
        FilePath robotReportsDir = getRobotReportsDir(new FilePath(build.getRootDir()));
        return robotReportsDir;
    }	
}