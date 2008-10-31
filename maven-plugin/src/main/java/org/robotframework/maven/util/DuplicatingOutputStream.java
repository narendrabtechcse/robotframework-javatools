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

import java.io.IOException;
import java.io.OutputStream;

public class DuplicatingOutputStream extends OutputStream {

    private final OutputStream stream1;

    private final OutputStream stream2;

    public DuplicatingOutputStream(OutputStream stream1,
            OutputStream stream2) {
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    public void write(int b) throws IOException {
        stream1.write(b);
        stream2.write(b);
    }
}
