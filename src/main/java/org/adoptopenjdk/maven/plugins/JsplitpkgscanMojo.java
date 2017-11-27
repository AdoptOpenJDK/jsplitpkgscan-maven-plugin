package org.adoptopenjdk.maven.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which:
 *
 * 1. touches a timestamp file. TODO remove this feature
 * 2. TODO Runs jsplitpkgscan
 */
@Mojo(name = "jsplitpkgscan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class JsplitpkgscanMojo extends AbstractMojo {

    // Location of the file.
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Default execute method for Maven plugins.
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

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(touch);

            fileWriter.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
