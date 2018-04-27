package org.adoptopenjdk.maven.plugins;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import javax.tools.Tool;
import java.io.File;

public class JsplitpkgscanMojoTest extends AbstractMojoTestCase {
    /**
     * {@inheritDoc}
     */
    protected void setUp()
            throws Exception {
        // required
        super.setUp();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown()
            throws Exception {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    public void testSomething()
            throws Exception {
        File pom = getTestFile("src/test/resources/unit/jsplitpkgscan-maven-plugin/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        JsplitpkgscanMojo jsplitpkgscanMojo = (JsplitpkgscanMojo) lookupMojo("jsplitpkgscan", pom);

        Tool tool = null;

//        jsplitpkgscanMojo.runJsplitpkgscan(tool);
    }
}
