package org.adoptopenjdk.maven.plugins;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputParser {
    private final BiConsumer<String, Set<ModuleDetail>> consumer;

    public OutputParser(BiConsumer<String, Set<ModuleDetail>> consumer) {
        this.consumer = consumer;
    }

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
