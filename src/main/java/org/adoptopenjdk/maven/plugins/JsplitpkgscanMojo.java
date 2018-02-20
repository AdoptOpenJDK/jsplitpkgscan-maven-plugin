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

import javax.tools.Tool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
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
        // Run jsplitpkgscan with the default arguments
        for (Tool tool : ServiceLoader.load(Tool.class)) {
            if ("jsplitpgkscan".equals(tool.toString())) {
                List<String> artifactJars = new ArrayList<>();
                //todo: add filter possibility to only include certain scopes
                collectArtifacts(artifact -> artifactJars.add(artifact.getFile().getAbsolutePath()));

                System.out.println(artifactJars);


                //todo: parse output to create errors
                tool.run(System.in, System.out, System.err, artifactJars.toArray(new String[0]));
            }
        }
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
