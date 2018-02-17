package org.adoptopenjdk.maven.plugins;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;

import javax.tools.Tool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Goal which:
 * <p>
 * TODO Runs jsplitpkgscan for all artifacts
 */
@Mojo(name = "jsplitpkgscan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class JsplitpkgscanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Execute jsplitpgkscan tool for the projects artifact against all it's dependencies.
     *
     * @throws MojoExecutionException
     */
    @Override
    public void execute() throws MojoExecutionException {
        ServiceLoader.load(Tool.class).stream()
                .map(toolProvider -> toolProvider.get())
                .filter(tool -> "jsplitpgkscan".equals(tool.toString()))
                .findFirst().ifPresent(this::runJsplitpkgscan);
    }

    private void runJsplitpkgscan(Tool tool) {
        List<String> artifactJars = new ArrayList<>();
        //todo: add filter possibility to only include certain scopes
        collectArtifacts(artifact -> artifactJars.add(artifact.getFile().getAbsolutePath()));

        getLog().debug("Artifacts being processed: " + artifactJars);

        //todo: parse output to create errors
        tool.run(System.in, System.out, System.err, artifactJars.toArray(new String[0]));
    }

    private void collectArtifacts(Consumer<Artifact> artifactConsumer) {
        // the projects own artifact
        artifactConsumer.accept(project.getArtifact());
        // the projects artifacts
        project.getArtifacts().forEach(artifactConsumer);  //todo: what kind of dependencies are here
        // the project dependency artifacts
        ArtifactRepository localRepository = session.getLocalRepository();
        for (Dependency dependency : project.getDependencies()) {
            artifactConsumer.accept(localRepository.find(createDefaultArtifact(dependency)));
        }
    }

    private static Artifact createDefaultArtifact(Dependency dep) {
        return new DefaultArtifact(dep.getGroupId(),
                dep.getArtifactId(),
                dep.getVersion(),
                dep.getScope(),
                dep.getType(),
                dep.getClassifier(),
                new DefaultArtifactHandler(dep.getType()));
    }
}
