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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Goal which:
 * <p>
 * 1. touches a timestamp file. TODO remove this feature
 * 2. TODO Runs jsplitpkgscan
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
     * Default execute method for Maven plugins.
     *
     * @throws MojoExecutionException
     */
    @Override
    public void execute() throws MojoExecutionException {
        // TODO We still create a text file until we can get jsplitpkgscan working correctly.
        createTouchTxtFile();

        // Run jsplitpkgscan with the default arguments
        // TODO figure out how to read in parameters set by the plugin configuraton
        // runJsplitpkgscan()
    }

    private void createTouchTxtFile() throws MojoExecutionException {
        File touchFile = outputDirectory;
        if (!touchFile.exists()) {
            boolean created = touchFile.mkdirs();
            if (created) {
                System.out.println(touchFile.getAbsolutePath() + " was created.");
            }
        }

        File touch = new File(touchFile, "touch.txt");
        try (FileWriter fileWriter = new FileWriter(touch); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            collectArtifacts(artifact -> printWriter.println(artifact.getFile()));
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        }
    }

    private void collectArtifacts(Consumer<Artifact> artifactConsumer) {

        // The project's own artifact
        Artifact projectArtifact = project.getArtifact();
        artifactConsumer.accept(projectArtifact);

        // The rest of the project's artifacts
        project.getArtifacts().forEach(artifactConsumer);

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
