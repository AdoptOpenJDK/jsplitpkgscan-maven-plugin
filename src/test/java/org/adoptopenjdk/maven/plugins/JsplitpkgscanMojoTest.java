package org.adoptopenjdk.maven.plugins;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.mockito.Mockito;

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
    public void testToolInvocation_with_project_artifact_only()
            throws Exception {
        File pom = getTestFile("src/test/resources/unit/jsplitpkgscan-maven-plugin/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        JsplitpkgscanMojo jsplitpkgscanMojo = (JsplitpkgscanMojo) lookupMojo("jsplitpkgscan", pom);
        assertNotNull(jsplitpkgscanMojo);
        String projectArtifact = jsplitpkgscanMojo.project.getArtifact().getFile().getAbsolutePath();

        Tool tool = Mockito.mock(Tool.class);

        jsplitpkgscanMojo.runJsplitpkgscan(tool);

        Mockito.verify(tool).run(System.in, System.out, System.err, projectArtifact);
    }
}
