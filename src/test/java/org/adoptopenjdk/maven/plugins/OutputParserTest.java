package org.adoptopenjdk.maven.plugins;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        BiConsumer<String, Set<ModuleDetail>> teeConsumer = (pkg,detail) -> {
            System.out.println(pkg + "\t" + detail);
        };

        OutputParser parser = new OutputParser(teeConsumer.andThen(consumer));
        parser.parse(outputData);

        // check what is going wrong
        verify(consumer).accept("CH.obj.Application.Base.Applic", setOf(new ModuleDetail(19, "file:/CH.obj.Application.jar"), new ModuleDetail(5, "file:/CH.obj.Core.jar")));
        verify(consumer).accept("CH.obj.Application.Base.General", setOf(new ModuleDetail(24,"file:/CH.obj.Application.jar"), new ModuleDetail(2,"file:/main-artifact.jar"), new ModuleDetail(1,"file:/CH.obj.di.jar")));
        verify(consumer).accept("CH.obj.Application.Base.Mandant", setOf(new ModuleDetail(12,"file:/CH.obj.Application.jar"), new ModuleDetail(1,"file:/CH.obj.Core.jar")));
        verify(consumer).accept("CH.obj.Application.Base.Organisation", setOf(new ModuleDetail(25,"file:/CH.obj.Application.jar"), new ModuleDetail(3,"file:/CH.obj.Core.jar")));

        verify(consumer, only()).accept(any(), any());
    }


    private Set<ModuleDetail> setOf(ModuleDetail... details) {
        return new HashSet<>(Arrays.asList(details));
    }

}
