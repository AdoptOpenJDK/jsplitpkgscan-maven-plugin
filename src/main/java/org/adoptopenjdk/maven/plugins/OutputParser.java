package org.adoptopenjdk.maven.plugins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the output of the jsplitpkgscan tool and provides a callback with the results.
 * The output is expected to be in a specific format where each line contains a package name
 * followed by module details.
 */
public class OutputParser {
    private final BiConsumer<String, Set<ModuleDetail>> consumer;

    /**
     * Constructs an OutputParser with a consumer that will handle the parsed results.
     *
     * @param consumer a BiConsumer that takes a package name and a set of ModuleDetail objects
     */
    public OutputParser(BiConsumer<String, Set<ModuleDetail>> consumer) {
        this.consumer = consumer;
    }

    /**
     * Parses the provided output data and invokes the consumer with the package name and module details.
     *
     * @param outputData the byte array containing the output data to parse
     * @throws IOException if an I/O error occurs while reading the output data
     */
    public void parse(byte[] outputData) throws IOException {
        Pattern pattern = Pattern.compile("^[\\s]+([0-9]+)[\\s]+(.*)$");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputData)))) {
            Set<ModuleDetail> details = new HashSet<>();
            String currentPackage = null;
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    // collect module details later
                    details.add(new ModuleDetail(Integer.parseInt(matcher.group(1)), matcher.group(2)));
                    continue;
                }
                if (!line.equals(currentPackage)) {
                    if (currentPackage != null) {
                        consumer.accept(currentPackage, details);
                    }
                    currentPackage = line;
                    details.clear();
                }
            }
            if (currentPackage != null) {
                consumer.accept(currentPackage, details);
            }
        }
    }
}
