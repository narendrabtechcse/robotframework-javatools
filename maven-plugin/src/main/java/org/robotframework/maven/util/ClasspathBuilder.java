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
