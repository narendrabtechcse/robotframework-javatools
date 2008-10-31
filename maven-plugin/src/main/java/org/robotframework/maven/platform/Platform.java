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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.robotframework.maven.log.MavenLog;
import org.robotframework.maven.util.IO;


/**
 * @author Lasse Koskela
 */
public abstract class Platform {

    public static String envOutput;

    public static Platform resolve() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") != -1) {
            return new Windows();
        } else {
            return new Unix();
        }
    }

    public static String parseVariableFromConsoleOutput(String name) {
        Pattern pattern = Pattern.compile("^" + name + "=(.*?)$",
                Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(envOutput);
        if (!matcher.find()) {
            StringBuffer msg = new StringBuffer(512);
            msg.append("Environment variable ").append(name);
            msg.append(" can not be propagated as it isn't");
            msg.append(" set in the current environment");
            if (MavenLog.isDebugEnabled()) {
                msg.append(":\n").append(envOutput);
                MavenLog.debug(msg);
            } else {
                MavenLog.warn(msg.toString());
            }
            return "";
        }
        return matcher.group(1);
    }

    private HashMap envVarsToPropagate;

    private static final File DEFAULT_WORKING_DIR = new File(".")
            .getAbsoluteFile();

    /**
     * Returns the name of the concrete <tt>Platform</tt> implementation.
     */
    public abstract String getName();

    /**
     * Prints out the environment variables on this platform.
     * 
     * @return E.g. "env" on UNIX and "set" on Windows
     * @throws IOException
     */
    protected abstract Execution printEnvironmentVariables()
            throws IOException;

    /**
     * Adds the platform-specific "prefix" to the given command. Typically, this
     * is some kind of a shell executable like "sh" in UNIX or "cmd.exe" on
     * Windows.
     * 
     * @param command
     *                The current command list.
     */
    protected abstract void addScriptPrefix(List command);

    protected void addScriptAndArguments(List command, String file,
            List args) {
        command.add(file);
        command.addAll(args);
    }

    public Platform() {
        envVarsToPropagate = new HashMap();
    }

    public Execution runExecutable(String file) throws IOException {
        return runExecutable(file, new ArrayList());
    }

    public Execution runExecutable(String file, List args)
            throws IOException {
        return runExecutable(file, args, DEFAULT_WORKING_DIR);
    }

    public Execution runExecutable(String file, List args,
            File workingDirectory) throws IOException {
        List command = new ArrayList();
        command.add(file);
        command.addAll(args);
        return execute(command, workingDirectory);
    }

    public Execution runScript(String file) throws IOException {
        return runScript(file, new ArrayList());
    }

    public Execution runScript(String file, List args)
            throws IOException {
        return runScript(file, args, DEFAULT_WORKING_DIR);
    }

    public Execution runScript(String file, List args,
            File workingDirectory) throws IOException {
        List command = new ArrayList();
        addScriptPrefix(command);
        addScriptAndArguments(command, file, args);
        return execute(command, workingDirectory);
    }

    protected Execution execute(List command, File workingDirectory)
            throws IOException {
        Execution exec = createExecution(command);
        exec.setWorkingDirectory(workingDirectory);
        if (getEnvironmentVariables() != null) {
            exec.setEnvironmentVariables(getEnvironmentVariables());
        }
        exec.run();
        return exec;
    }

    protected Execution createExecution(List command) {
        return new Execution(command);
    }

    protected String[] getEnvironmentVariables() {
        if (envVarsToPropagate.isEmpty()) {
            return null;
        }
        List entries = new ArrayList();
        for (Iterator i = envVarsToPropagate.keySet().iterator(); i
                .hasNext();) {
            String name = (String) i.next();
            entries.add(name + "=" + envVarsToPropagate.get(name));
        }
        return (String[]) entries.toArray(new String[entries.size()]);
    }

    public void propagateEnvironmentVariable(String name) {
        setEnvironmentVariable(name, getEnvironmentVariable(name));
    }

    public void setEnvironmentVariable(String name, String value) {
        envVarsToPropagate.put(name, value);
    }

    /**
     * Returns the given environment variable from the underlying operating
     * system.
     * 
     * @param name
     *                Environment variable name
     * @return Value of the environment variable, or an empty string if the
     *         variable is not set.
     */
    public String getEnvironmentVariable(String name) {
        if (System.getProperty(name) != null) {
            return System.getProperty(name);
        }
        getEnvironmentVariablesFromSystem();
        return parseVariableFromConsoleOutput(name);
    }

    protected synchronized void getEnvironmentVariablesFromSystem() {
        if (envOutput == null) {
            try {
                Execution execution = printEnvironmentVariables();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }
                envOutput = IO.readToString(execution.getStdout());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("(Platform: ").append(getName()).append(") ");
        String[] envVars = getEnvironmentVariables();
        for (int i = 0; i < envVars.length; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(envVars[i]);
        }
        return s.toString();
    }
}
