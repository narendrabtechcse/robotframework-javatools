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

package org.robotframework.javalib.beans.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Finds resources according to given pattern, resolves them into names
 * and then loads them.
 *
 * @author Sami Honkonen
 */
public class DefaultClassFinder extends PathMatchingResourcePatternResolver implements ClassFinder {
    private ClassNameResolver classNameResolver;

    /**
     * @param classLoader resolver to use for resolving the given pattern
     */
    public DefaultClassFinder(ClassLoader classLoader) {
        super(classLoader);
        this.classNameResolver = new DefaultClassNameResolver();
    }

    public DefaultClassFinder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Uses the resolver provided in the constructor with the given
     * pattern to get matching classes. By default uses
     * {@link DefaultClassNameResolver} to resolve {@link Resource}
     * objects to class names.
     */
    public Class[] getClasses(String pattern) {
        Resource[] resources = findResources(pattern);
        List classes = new ArrayList();

        for (int i = 0; i < resources.length; i++) {
            String className = classNameResolver.resolveClassName(resources[i], determinePackageRoot(pattern));
            classes.add(loadClass(className));
        }

        return (Class[]) classes.toArray(new Class[0]);
    }

    /**
     * The default {@link ClassNameResolver} is
     * {@link DefaultClassNameResolver}. Must be set prior to calling
     * {@link #getClasses(String, String)}.
     *
     * @param classNameResolver new ClassNameResolver
     */
    public void setClassNameResolver(ClassNameResolver classNameResolver) {
        this.classNameResolver = classNameResolver;
    }

    private String determinePackageRoot(String location) {
        String rootDir = determineRootDir(location);
        return rootDir.substring(rootDir.indexOf(":") + 1);
    }

    private Class loadClass(String className) {
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Resource[] findResources(String pattern) {
        try {
            return getResources(pattern);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
