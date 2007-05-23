/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbunit;

import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.executeUpdateQuietly;
import static org.unitils.core.util.SQLUtils.getItemAsString;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.dbunit.dataset.IDataSet;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetoperation.DataSetOperation;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModuleDataSetOperationTest extends UnitilsJUnit3 {

    private DbUnitModule dbUnitModule;

    @TestDataSource
    private DataSource dataSource = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTables();
        createTestTables();

        MockDataSetOperation.operationExecuted = false;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        dropTestTables();
    }

    public void testLoadDataSet_defaultDataSetOperation() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod1"));
        assertLoadedDataSet("DbUnitModuleDataSetOperationTest$DataSetTest.xml");
    }

    public void testLoadDataSet_customDataSetOperation() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethodCustomDataSetOperation"));
        assertTrue(MockDataSetOperation.operationExecuted);
    }

    /**
     * Utility method to assert that the correct data set was loaded.
     *
     * @param expectedDataSetName the name of the data set, not null
     */
    private void assertLoadedDataSet(String expectedDataSetName) throws SQLException {
        String dataSet = getItemAsString("select dataset from test", dataSource);
        assertEquals(expectedDataSetName, dataSet);
    }

    /**
     * Creates the test tables.
     */
    private void createTestTables() throws SQLException {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST(dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
    }

    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        public void testMethod1() {
        }

        @DataSet(operation = MockDataSetOperation.class)
        public void testMethodCustomDataSetOperation() {
        }
    }

    public static class MockDataSetOperation implements DataSetOperation {

        private static boolean operationExecuted;

        public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
            operationExecuted = true;
        }
    }
}
