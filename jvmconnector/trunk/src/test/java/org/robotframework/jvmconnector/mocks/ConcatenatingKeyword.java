package org.robotframework.jvmconnector.mocks;

import org.robotframework.javalib.keyword.DocumentedKeyword;

public class ConcatenatingKeyword implements DocumentedKeyword {
    public Object execute(Object[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : arguments) {
            sb.append(arg);
        }
        return sb.toString();
    }

    public String[] getArgumentNames() {
        throw new UnsupportedOperationException("");
    }

    public String getDocumentation() {
        throw new UnsupportedOperationException("");
    }
}
