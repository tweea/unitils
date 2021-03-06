package org.unitils.dbunit.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;

/**
 * Test {@link FileHandler#createTempFile(String)}.
 * 
 * @author wiw
 * @since 3.4
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class FileHandlerCreateFileTest {
    private FileHandler sut;

    @Before
    public void setUp() {
        sut = new FileHandler();
    }

    @Test
    public void testCreateFile() {
        String resourceName = "/org/unitils/testdata/exampleResourceData.xml";
        File actual = sut.createTempFile(resourceName);

        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getName().toLowerCase().startsWith("exampleresourcedata-"));

        // delete temp file
        actual.delete();
    }

    @Test
    public void testCreateFileWithSlashAtTheEnd()
        throws Exception {
        String resourceName = "/org/unitils/testdata/exampleResourceData.xml/";
        File actual = sut.createTempFile(resourceName);

        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getName().toLowerCase().startsWith("exampleresourcedata-"));

        // delete temp file
        actual.delete();
    }

    @Test
    public void testCreateFileWithoutSlashes()
        throws Exception {
        String resourceName = "exampleResourceData.xml";
        File actual = sut.createTempFile(resourceName);

        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getName().toLowerCase().startsWith("exampleresourcedata-"));

        // delete temp file
        actual.delete();
    }

    @Test
    public void testUnableToCreateFile() {
        String resourceName = "1.xml";
        Assert.assertNull(sut.createTempFile(resourceName));
    }
}
