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

package org.robotframework.javalib.reflection;

import org.robotframework.javalib.util.ArrayUtil;

/**
 * @author Heikki Hulkko
 */
public class ArgumentGrouper implements IArgumentGrouper {
    private final Class<?>[] parameterTypes;

    public ArgumentGrouper(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] groupArguments(Object[] ungroupedArguments) {
        if (ungroupedArguments == null || ungroupedArguments.length == 0 ||
            parameterTypes.length == ungroupedArguments.length && lastArgIsNotAnArray()) {
            return ungroupedArguments;
        }
        return extractArguments(asStrings(ungroupedArguments));
    }

    private String[] asStrings(Object[] ungroupedArguments) {
        String[] argsAsString = new String[ungroupedArguments.length];
        for (int i = 0; i < ungroupedArguments.length; i++) {
            argsAsString[i] = ungroupedArguments[i].toString();
        }
        return argsAsString;
    }

    private Object[] extractArguments(String[] ungroupedArguments) {
        Object[] beginningOfarguments = extractBeginningOfArguments(ungroupedArguments);
        Object[] extractedArguments = new Object[beginningOfarguments.length + 1];
        for (int i = 0; i < beginningOfarguments.length; i++) {
            extractedArguments[i] = beginningOfarguments[i];
        }

        extractedArguments[extractedArguments.length - 1] = extractRestOfArguments(ungroupedArguments);
        return extractedArguments;
    }

    private Object[] extractBeginningOfArguments(Object[] ungroupedArguments) {
        return ArrayUtil.copyOfRange(ungroupedArguments, 0, parameterTypes.length - 1);
    }

    private String[] extractRestOfArguments(String[] ungroupedArguments) {
        return ArrayUtil.copyOfRange(ungroupedArguments, parameterTypes.length - 1, ungroupedArguments.length);
    }

    private boolean lastArgIsNotAnArray() {
        return !parameterTypes[parameterTypes.length - 1].isArray();
    }
}
