package org.adoptopenjdk.maven.plugins;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.easymock.EasyMock.*;
import static org.mockito.Mockito.only;

public class OutputParserTest {
    private byte[] outputData;

    @Before
    public void setUp() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/jsplitpkgscan.out");
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            outputData = out.toByteArray();
        }
    }

    @Test
    public void testParse_packages() throws IOException {
        BiConsumer<String, Set<ModuleDetail>> consumer = mock(BiConsumer.class);

        OutputParser parser = new OutputParser(consumer);

        consumer.accept("org.adoptopenjdk.app", setOf(new ModuleDetail(19, "file:/org.adoptopenjdk.app.jar"), new ModuleDetail(5, "file:/org.adoptopenjdk.core.jar")));
        consumer.accept("org.adoptopenjdk.base", setOf(new ModuleDetail(24,"file:/org.adoptopenjdk.app.jar"), new ModuleDetail(2,"file:/main-artifact.jar"), new ModuleDetail(1,"file:/org.adoptopenjdk.util.jar")));
        consumer.accept("org.adoptopenjdk.app.util", setOf(new ModuleDetail(12,"file:/org.adoptopenjdk.app.jar"), new ModuleDetail(1,"file:/org.adoptopenjdk.util.jar")));
        consumer.accept("org.adoptopenjdk.model", setOf(new ModuleDetail(25,"file:/org.adoptopenjdk.app.jar"), new ModuleDetail(3,"file:/org.adoptopenjdk.model.jar")));

        replay(consumer);

        parser.parse(outputData);

        verify(consumer);
    }

    private Set<ModuleDetail> setOf(ModuleDetail... details) {
        return new HashSet<>(Arrays.asList(details));
    }

}
