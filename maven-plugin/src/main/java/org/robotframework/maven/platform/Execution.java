/* Copyright 2008 Nokia Siemens Networks Oyj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robotframework.maven.platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.robotframework.maven.log.MavenLog;
import org.robotframework.maven.util.DuplicatingOutputStream;
import org.robotframework.maven.util.ID;
import org.robotframework.maven.util.IO;


public class Execution implements Runnable {

    private static final int DEFAULT_TIMEOUT = 3 * 60 * 60 * 1000;

    private Process process;

    private int exitCode = -1;

    private String[] command;

    protected String[] environmentVariables;

    private File workingDirectory;

    private boolean completed;

    private long executionId;

    protected File stderrFile;

    protected File stdoutFile;

    private int timeout = DEFAULT_TIMEOUT;

    private boolean consoleOutputEnabled;

    public Execution(List command) {
        this(command, null);
    }

    public Execution(List command, String[] environmentVariables) {
        this(command, environmentVariables, new File("."));
    }

    public Execution(List command, String[] environmentVariables,
            File workingDirectory) {
        this.executionId = ID.next();
        this.command = toStringArray(command);
        this.environmentVariables = environmentVariables;
        this.workingDirectory = workingDirectory;
        reset();
    }

    private void reset() {
        stdoutFile = createTempFile("stdout");
        stderrFile = createTempFile("stderr");
        completed = false;
        consoleOutputEnabled = false;
    }

    private File createTempFile(String identifier) {
        try {
            String prefix = "Execution-" + executionId + "-";
            String suffix = "." + identifier + ".log";
            File file = File.createTempFile(prefix, suffix);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWorkingDirectory(File directory) {
        workingDirectory = directory;
    }

    public void setEnvironmentVariables(String[] env) {
        this.environmentVariables = env;
    }

    public void run() {
        try {
            begin();
            waitForCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void begin() throws IOException {
        MavenLog.debug("Execution " + executionId + ": "
                + Arrays.asList(command) + " (in " + workingDirectory
                + ")");
        Runtime runtime = Runtime.getRuntime();
        if (environmentVariables == null) {
            process = runtime.exec(command, null, workingDirectory);
        } else {
            process = runtime.exec(command, environmentVariables,
                    workingDirectory);
        }
    }

    public void waitForCompletion() {
        if (completed) {
            return;
        }
        new TimeoutOperation(timeout, new Runnable() {
            public void run() {
                try {
                    MavenLog.debug("Directing stdout and stderr to "
                            + stdoutFile.getAbsolutePath() + " and "
                            + stderrFile.getAbsolutePath());
                    consume(process.getErrorStream(), stderrFile);
                    consume(process.getInputStream(), stdoutFile);
                } catch (FileNotFoundException cannotHappen) {
                    throw new RuntimeException(cannotHappen);
                }
                try {
                    MavenLog.debug("Waiting for process "
                            + executionId + " to complete...");
                    exitCode = process.waitFor();
                    completed = true;
                    MavenLog
                            .debug("Process " + executionId
                                    + " completed with exit code "
                                    + exitCode);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    public int getExitCode() {
        waitForCompletion();
        return exitCode;
    }

    public InputStream getStdout() {
        waitForCompletion();
        return getOutputFrom(stdoutFile);
    }

    public InputStream getStderr() {
        waitForCompletion();
        return getOutputFrom(stderrFile);
    }

    private InputStream getOutputFrom(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] toStringArray(List list) {
        if (list == null) {
            return new String[0];
        }
        List strings = new ArrayList();
        for (Iterator i = list.iterator(); i.hasNext();) {
            strings.add(String.valueOf(i.next()));
        }
        return (String[]) strings.toArray(new String[strings.size()]);
    }

    public File getStdoutFile() {
        return stdoutFile;
    }

    public void setConsoleOutputEnabled(boolean enabled) {
        consoleOutputEnabled = enabled;
    }

    private void consume(InputStream what, File where)
            throws FileNotFoundException {
        OutputStream stream = new FileOutputStream(where);
        if (consoleOutputEnabled) {
            stream = new DuplicatingOutputStream(stream, System.out);
        }
        IO.consume(what, stream);
    }
}
