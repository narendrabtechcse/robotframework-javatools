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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lasse Koskela
 */
public class ClasspathBuilder {

    private static final String SEPARATOR = System
            .getProperty("path.separator");

    private List elements;

    public ClasspathBuilder(boolean includeSystemClasspath) {
        this.elements = new ArrayList();
        if (includeSystemClasspath) {
            add(System.getProperty("java.class.path")
                    .split(SEPARATOR));
        }
    }

    public void add(String[] elements) {
        add(Arrays.asList(elements));
    }

    public void add(Collection elements) {
        for (Iterator i = elements.iterator(); i.hasNext();) {
            add(i.next());
        }
    }

    public void add(Object element) {
        String entry = Path.to(element);
        if (!elements.contains(entry)) {
            elements.add(entry);
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        for (Iterator i = elements.iterator(); i.hasNext();) {
            s.append(i.next());
            if (i.hasNext()) {
                s.append(SEPARATOR);
            }
        }
        return s.toString();
    }

    public void addContainerOf(Class c) {
        String path = "/" + c.getName().replace('.', '/') + ".class";
        add(Path.to(c.getResource(path)));
    }

}
