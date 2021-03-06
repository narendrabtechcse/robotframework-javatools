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

package org.robotframework.jvmconnector.common;

import java.io.Serializable;

/**
 * Wraps keyword execution results, logging output and exceptions it throws.
 * 
 * @author Heikki Hulkko
 */
public interface KeywordExecutionResult extends Serializable {
    /**
     * Returns the result of keyword execution.
     */
    Object getResult();

    /**
     * Returns all that keyword printed to STDOUT with java.lang.System.out.
     */
    String getStdOutAsString();

    /**
     * Returns all that keyword printed to STDERR with java.lang.System.err.
     */
    String getStdErrAsString();

    /**
     * Returns a boolean indicating keyword success or failure.
     */
    boolean isKeywordPassed();

    /**
     * @return TestFailedException wrapping the cause of the failure
     */
    TestFailedException getTestFailedException();
}
