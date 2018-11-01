package org.adoptopenjdk.maven.plugins;

import junit.framework.AssertionFailedError;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.mockito.ArgumentMatcher;

import static org.mockito.Mockito.*;

import javax.tools.Tool;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class JsplitpkgscanMojoTest extends AbstractMojoTestCase {
    private Tool tool;
    private JsplitpkgscanMojo jsplitpkgscanMojo;

    @Override
    protected void setUp()
            throws Exception {
        super.setUp();
        tool = mock(Tool.class);
        File pom = getTestFile("src/test/resources/unit/jsplitpkgscan-maven-plugin/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        jsplitpkgscanMojo = (JsplitpkgscanMojo) lookupMojo("jsplitpkgscan", pom);
    }

    public void testToolInvocation_with_project_artifact_only() {
        jsplitpkgscanMojo.runJsplitpkgscan(tool);

        verify(tool).run(any(), any(), any(), endsWith("main-artifact.jar"));
    }

    public void testToolInvocation_with_project_artifact_only_having_output() {
        when(tool.run(any(), argThat(new OutputStreamMatcher()), any(), endsWith("main-artifact.jar"))).thenReturn(0);

        jsplitpkgscanMojo.runJsplitpkgscan(tool);
    }

    public void testToolInvocation_with_project_artifact_only_and_dependencies() {
        List<Dependency> dependencies = jsplitpkgscanMojo.project.getDependencies();
        dependencies.add(createDependency("compile"));
        dependencies.add(createDependency("runtime"));
        dependencies.add(createDependency("test"));

        jsplitpkgscanMojo.runJsplitpkgscan(tool);

        verify(tool).run(any(), any(), any(), endsWith("main-artifact.jar"), endsWith("compile-lib-1.0.0.jar"), endsWith("runtime-lib-1.0.0.jar"));
    }

    public void testToolInvocation_with_project_artifact_and_additional_artifacts() {
        Set<Artifact> artifacts = jsplitpkgscanMojo.project.getArtifacts();
        artifacts.add(createArtifact("additional-one", "jar"));
        artifacts.add(createArtifact("additional-two", "jar"));

        jsplitpkgscanMojo.runJsplitpkgscan(tool);

        verify(tool).run(any(), any(), any(), endsWith("main-artifact.jar"), endsWith("additional-one.jar"), endsWith("additional-two.jar"));
    }

    static class OutputStreamMatcher implements ArgumentMatcher<OutputStream> {
        @Override
        public boolean matches(OutputStream outputStream) {
            try {
                File outFile = getTestFile("src/test/resources/jsplitpkgscan.out");
                Files.copy(outFile.toPath(), outputStream);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
            return true;
        }
    }

    private static ArtifactStub createArtifact(String artifactId, String type) {
        ArtifactStub artifact = new ArtifactStub();
        artifact.setArtifactId(artifactId);
        artifact.setGroupId("group-id");
        artifact.setVersion("1.0.0");
        artifact.setScope("runtime");
        artifact.setType(type);
        artifact.setFile(new File(artifactId + "." + type));
        return artifact;
    }

    private static Dependency createDependency(String scope) {
        Dependency dependency = new Dependency();
        dependency.setArtifactId(scope + "-lib");
        dependency.setGroupId("some-group");
        dependency.setVersion("1.0.0");
        dependency.setScope(scope);
        dependency.setType("jar");
        dependency.setClassifier("");
        return dependency;
    }
}

