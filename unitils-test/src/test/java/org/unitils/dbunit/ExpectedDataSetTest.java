/*
 * Copyright 2008, Unitils.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbunit;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.unitils.database.SQLUnitils.executeUpdate;

/**
 * Tests the expected data set behavior.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ExpectedDataSetTest
    extends UnitilsJUnit4 {
    private static final Logger LOG = LoggerFactory.getLogger(ExpectedDataSetTest.class);

    /* Tested object */
    private DbUnitModule dbUnitModule;

    /* The dataSource */
    @TestDataSource
    private DataSource dataSource = null;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTable();
        createTestTable();
    }

    /**
     * Clean-up test database.
     */
    @After
    public void tearDown()
        throws Exception {
        dropTestTable();
    }

    /**
     * Test for no annotation on method-level and default annotation on class-level
     */
    @Test
    public void testEqual()
        throws Exception {
        assertEqualDataSet("testMethod1");
    }

    /**
     * Test for custom annotation on method-level overriding default annotation on class-level
     */
    @Test
    public void testCustomDataSet()
        throws Exception {
        assertEqualDataSet("testMethod2");
    }

    /**
     * Test for default annotation on method-level and default annotation on class-level
     */
    @Test
    public void testClassAndMethodAnnotation()
        throws Exception {
        assertEqualDataSet("testMethod3");
    }

    /**
     * Test for expected dataset that does not correspond to the content of the database. Assertion should
     * have failed.
     */
    @Test
    public void testDifferentContent()
        throws Exception {
        assertDifferentDataSet("testMethod4");

        // check whether the loading of the data set was not rolled back
        assertEqualDataSet("testMethod2");
    }

    /**
     * Test for default file that is not found
     */
    @Test(expected = UnitilsException.class)
    public void testFileNotFound()
        throws Exception {
        assertEqualDataSet("testNotFound1");
    }

    /**
     * Test for custom file that is not found
     */
    @Test(expected = UnitilsException.class)
    public void testCustomDataSetFileNotFound()
        throws Exception {
        assertEqualDataSet("testNotFound2");
    }

    /**
     * Test for no annotation on method-level and custom annotation on class-level
     */
    @Test
    public void testCustomDataSetOnClassAnnotation()
        throws Exception {
        assertEqualDataSet("testMethod1");
    }

    /**
     * Test for default annotation on method-level overriding a custom annotation on class-level
     */
    @Test
    public void testCustomClassAnnotationOverridenByDefaultMethodAnnotation()
        throws Exception {
        assertEqualDataSet("testMethod2");
    }

    /**
     * Test with a dataset containing an explicit null value ([null])
     * This used to give a NullPointerException (UNI-148)
     */
    @Test
    public void testExplicitNullValue()
        throws Exception {
        assertEqualDataSet("nullValue");
    }

    private void assertDifferentDataSet(String methodName) {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertEqualDataSet(methodName));
        assertThat(error).as("No differences found for dataset. Method name: " + methodName).isNotNull();
    }

    private void assertEqualDataSet(String methodName)
        throws Exception {
        Object testObject = new TestClass();
        Method testMethod = TestClass.class.getMethod(methodName);
        dbUnitModule.insertDataSet(testMethod, testObject);
        dbUnitModule.assertDbContentAsExpected(testMethod, testObject);
    }

    /**
     * Test class with a class level expected dataset.
     * The data set annotations are used to initialize the database content.
     */
    @ExpectedDataSet
    public class TestClass {
        @DataSet("ExpectedDataSetTest$TestClass.testMethod1-result.xml")
        public void testMethod1() {
        }

        @DataSet("CustomDataSet.xml")
        @ExpectedDataSet("CustomDataSet.xml")
        public void testMethod2() {
        }

        @DataSet("ExpectedDataSetTest$TestClass.testMethod3-result.xml")
        @ExpectedDataSet
        public void testMethod3() {
        }

        @DataSet("CustomDataSet.xml")
        @ExpectedDataSet
        public void testMethod4() {
        }

        public void testNotFound1() {
        }

        @ExpectedDataSet("xxxxxx.xml")
        public void testNotFound2() {
        }

        @DataSet("ExpectedDataSetTest-nullValue.xml")
        @ExpectedDataSet("ExpectedDataSetTest-nullValue.xml")
        public void nullValue() {
        }
    }

    /**
     * Test class with a custom class level expected dataset
     * The data set annotations are used to initialize the database content.
     */
    @ExpectedDataSet("CustomDataSet.xml")
    public class CustomTestClass {
        @DataSet("CustomDataSet.xml")
        public void testMethod1() {
        }

        @DataSet("ExpectedDataSetTest$CustomTestClass.testMethod2-result.xml")
        @ExpectedDataSet
        public void testMethod2() {
        }
    }

    /**
     * Utility method to create the test table.
     */
    private void createTestTable() {
        executeUpdate("create table TEST(dataset varchar(100))", dataSource);
    }

    /**
     * Removes the test database table
     */
    private void dropTestTable() {
        try {
            executeUpdate("drop table TEST", dataSource);
        } catch (UnitilsException e) {
            LOG.trace("", e);
            // Ignored
        }
    }
}
