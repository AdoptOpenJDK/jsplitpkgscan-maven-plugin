package org.adoptopenjdk.maven.plugins;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class ModuleDetailTest {
    private ModuleDetail info_with_no_class;
    private ModuleDetail info_with_one_class;
    private ModuleDetail info_with_two_classes;

    @Before
    public void setUp() {
        info_with_no_class = new ModuleDetail(0, "someplaceA");
        info_with_one_class = new ModuleDetail(1, "someplaceB");
        info_with_two_classes= new ModuleDetail(2, "someplaceC");
    }

    @Test
    public void testToString() {
        assertEquals("someplaceA (0 classes in that package)", info_with_no_class.toString());
        assertEquals("someplaceB (1 class in that package)", info_with_one_class.toString());
        assertEquals("someplaceC (2 classes in that package)", info_with_two_classes.toString());
    }
}
