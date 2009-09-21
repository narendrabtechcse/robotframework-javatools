package org.robotframework.jvmconnector.mocks;

import org.robotframework.javalib.keyword.Keyword;

public class ConcatenatingKeyword implements Keyword {
    public Object execute(Object[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : arguments) {
            sb.append(arg);
        }
        return sb.toString();
    }
}
