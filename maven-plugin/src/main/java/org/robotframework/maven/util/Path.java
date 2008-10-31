package org.robotframework.maven.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;

/**
 * @author Lasse Koskela
 */
public class Path {

    private static class Resolver {

        private static final List resolvers = new ArrayList();
        static {
            resolvers.add(new Resolver(File.class) {
                protected String resolve(Object obj) {
                    return ((File) obj).getAbsolutePath();
                }
            });
            resolvers.add(new Resolver(Artifact.class) {
                protected String resolve(Object obj) {
                    return ((Artifact) obj).getFile()
                            .getAbsolutePath();
                }
            });
            resolvers.add(new Resolver(URL.class) {
                protected String resolve(Object obj) {
                    URL url = (URL) obj;
                    String path = url.toExternalForm();
                    if (path.indexOf('!') != -1) {
                        path = path.substring(0, path.indexOf('!'));
                    }
                    if (path.startsWith("jar:")) {
                        path = path.substring("jar:".length());
                    }
                    if (path.startsWith("file:")) {
                        path = path.substring("file:".length());
                    }
                    return path;
                }
            });
            resolvers.add(new Resolver(Object.class));
        }

        private static Resolver forElement(Object obj) {
            for (Iterator i = resolvers.iterator(); i.hasNext();) {
                Resolver resolver = (Resolver) i.next();
                if (resolver.accepts(obj)) {
                    return resolver;
                }
            }
            return null;
        }

        public Resolver(Class supportedInterface) {
            this.supportedInterface = supportedInterface;
        }

        public boolean accepts(Object obj) {
            return supportedInterface
                    .isAssignableFrom(obj.getClass());
        }

        protected String resolve(Object obj) {
            return String.valueOf(obj);
        }

        private final Class supportedInterface;
    }

    public static String to(Object obj) {
        return Resolver.forElement(obj).resolve(obj);
    }

}
