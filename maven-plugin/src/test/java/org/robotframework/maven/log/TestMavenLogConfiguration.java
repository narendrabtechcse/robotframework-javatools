package org.robotframework.maven.log;

import junit.framework.TestCase;

import org.apache.maven.plugin.dependency.utils.SilentLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.robotframework.maven.log.MavenLog;
import org.robotframework.maven.testutils.AssertableFlag;


public class TestMavenLogConfiguration extends TestCase {

    public void testLogImplementationCanBeConfigured()
            throws Exception {
        final AssertableFlag flag = new AssertableFlag();
        Log log = new SilentLog() {
            public void debug(CharSequence content) {
                flag.set();
            }
        };
        MavenLog.setLog(log);
        MavenLog.debug("hello");
        flag.shouldBeSet();
    }

    public void testLogImplementationCanBeAccessedLater()
            throws Exception {
        Log log = new SystemStreamLog();
        MavenLog.setLog(log);
        assertSame(log, MavenLog.getLog());
    }
}
