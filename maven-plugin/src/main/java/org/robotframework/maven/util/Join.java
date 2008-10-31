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
