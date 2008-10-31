package org.robotframework.maven.util;

public class Join {

    public static JoinPieceSelector lines(String[] lines) {
        return new JoinPieceSelector(lines);
    }

    public static class JoinPieceSelector {

        private final String[] lines;

        public JoinPieceSelector(String[] lines) {
            this.lines = lines;
        }

        public String with(String joinString) {
            StringBuffer s = new StringBuffer(1024);
            for (int i = 0; i < lines.length; i++) {
                if (i > 0) {
                    s.append(joinString);
                }
                s.append(lines[i]);
            }
            return s.toString();
        }
    }
}
