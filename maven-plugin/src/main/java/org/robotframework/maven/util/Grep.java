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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Grep {

    public static class ExactGrep extends Grep {
        private final String text;

        public ExactGrep(String text) {
            this.text = text;
        }

        protected boolean matches(String line) {
            return line.indexOf(text) != -1;
        }
    }

    public static class PatternGrep extends Grep {
        private final Pattern pattern;

        public PatternGrep(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        protected boolean matches(String line) {
            return pattern.matcher(line).matches();
        }
    }

    public String[] from(String input) {
        List matches = new ArrayList();
        String[] lines = input.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (matches(line)) {
                matches.add(line);
            }
        }
        return (String[]) matches.toArray(new String[matches.size()]);
    }

    protected abstract boolean matches(String line);

    public static Grep exact(String text) {
        return new ExactGrep(text);
    }

    public static Grep pattern(String regex) {
        return new PatternGrep(regex);
    }
}
