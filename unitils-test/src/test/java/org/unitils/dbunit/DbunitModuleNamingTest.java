package org.unitils.dbunit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.annotation.UsedForTesting;
import org.unitils.dbunit.dataset.ColumnComparisonTest;
import org.unitils.dbunit.util.DataSetFileNamesHandler;

/**
 * DbunitModuleNaming.
 *
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
public class DbunitModuleNamingTest {
    private DataSetFileNamesHandler sut;

    @Before
    public void setUp() {
        sut = new DataSetFileNamesHandler();
    }

    /**
     * Test method for {@link DataSetFileNamesHandler#getDefaultDatasetBasedOnFilename(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetCorrectFileNameOnMethodLevel()
        throws SecurityException, NoSuchMethodException {
        JustAClass obj = new JustAClass();

        String actual = sut.getDefaultDatasetBasedOnFilename(obj.getClass(), obj.getClass().getMethod("method1"), "xml");
        String expected = "org.unitils.dbunit.JustAClass-method1.xml";

        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for {@link DataSetFileNamesHandler#getDefaultDatasetBasedOnFilename(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetCorrectFileNameOnClassLevel()
        throws SecurityException, NoSuchMethodException {
        ExpectedDataSetWithPrimaryKeysTest obj = new ExpectedDataSetWithPrimaryKeysTest();

        String actual = sut.getDefaultDatasetBasedOnFilename(obj.getClass(), obj.getClass().getMethod("setUp"), "xml");
        String expected = "org/unitils/dbunit/ExpectedDataSetWithPrimaryKeysTest.xml";

        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for {@link DataSetFileNamesHandler#getDefaultDataSetFileNameClassLevel(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameClassLevel_Innerclass() {
        TestClass1 obj = new TestClass1();
        String actual = sut.getDefaultDataSetFileNameClassLevel(obj.getClass(), "xml");
        String expected = "org/unitils/dbunit/DbunitModuleNamingTest$TestClass1.xml";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDefaultDataSetFileNameClassLevel() {
        ColumnComparisonTest obj = new ColumnComparisonTest();
        String actual = sut.getDefaultDataSetFileNameClassLevel(obj.getClass(), "xml");
        String expected = "org/unitils/dbunit/dataset/ColumnComparisonTest.xml";
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link DataSetFileNamesHandler#getDefaultDataSetFileNameMethodLevel(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameMethodLevel_InnerClass()
        throws SecurityException, NoSuchMethodException {
        TestClass1 obj = new TestClass1();
        String actual = sut.getDefaultDataSetFileNameMethodLevel(obj.getClass(), obj.getClass().getMethod("testMethod"), "xml");
        String expected = "DbunitModuleNamingTest$TestClass1-testMethod.xml";
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link DataSetFileNamesHandler#getDefaultDataSetFileNameMethodLevel(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameMethodLevel()
        throws SecurityException, NoSuchMethodException {
        ColumnComparisonTest obj = new ColumnComparisonTest();
        String actual = sut.getDefaultDataSetFileNameMethodLevel(obj.getClass(), obj.getClass().getMethod("equalStringValue"), "xml");
        String expected = "ColumnComparisonTest-equalStringValue.xml";
        Assert.assertEquals(expected, actual);
    }

    private class TestClass1 {
        @UsedForTesting
        public void testMethod() {
        }
    }
}
