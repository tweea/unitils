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
package org.unitils.dbunit.util;

import java.io.File;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Test for {@link MultiSchemaXmlDataSetReader}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetReaderTest
    extends UnitilsJUnit4 {

    /* Tested object */
    private MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader;

    /**
     * Creates the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader("SCHEMA_A");
    }

    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    @Test
    public void testLoadDataSet_lessColumnsLast()
        throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("LessColumnsLastDataSet.xml")));

        // there should be 1 dataset for the default schema A
        assertLenientEquals(new String[] {
            "SCHEMA_A"
        }, result.getSchemaNames());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSet.getTableNames());
        assertEquals(2, dataSet.getTable("TABLE_A").getRowCount());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(2, table.getRowCount());
        assertPropertyLenientEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));
    }

    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    @Test
    public void testLoadDataSet_lessColumnsFirst()
        throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("LessColumnsFirstDataSet.xml")));

        // there should be 1 dataset for the default schema A
        assertLenientEquals(new String[] {
            "SCHEMA_A"
        }, result.getSchemaNames());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSet.getTableNames());
        assertEquals(2, dataSet.getTable("TABLE_A").getRowCount());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyLenientEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals(2, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));
    }

    /**
     * Test the loading of a data set with 3 schemas:
     * schema D (overrides default schema A) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void testLoadDataSet_multiSchema()
        throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("MultiSchemaDataSet.xml")));

        // there should be 3 schemas
        assertLenientEquals(new String[] {
            "SCHEMA_D", "SCHEMA_B", "SCHEMA_C"
        }, result.getSchemaNames());

        // schema D should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_D");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetA.getTableNames());
        assertEquals(3, dataSetA.getTable("TABLE_A").getRowCount());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetB.getTableNames());
        assertEquals(2, dataSetB.getTable("TABLE_A").getRowCount());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetC.getTableNames());
        assertEquals(2, dataSetC.getTable("TABLE_A").getRowCount());
    }

    /**
     * Test the loading of a data set with 3 schemas:
     * schema A (default) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void testLoadDataSet_multiSchemaNoDefault()
        throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("MultiSchemaNoDefaultDataSet.xml")));

        // there should be 3 schemas
        assertLenientEquals(new String[] {
            "SCHEMA_A", "SCHEMA_B", "SCHEMA_C"
        }, result.getSchemaNames());

        // default schema A should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetA.getTableNames());
        assertEquals(3, dataSetA.getTable("TABLE_A").getRowCount());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetB.getTableNames());
        assertEquals(2, dataSetB.getTable("TABLE_A").getRowCount());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, dataSetC.getTableNames());
        assertEquals(2, dataSetC.getTable("TABLE_A").getRowCount());
    }

    /**
     * Test the loading of a data set out of 2 files:
     * this will load the LessColumnsLastDataSet.xml and LessColumnsFirstDataSet.xml dataset
     */
    @Test
    public void testLoadDataSet_multiInputStreams()
        throws Exception {
        File file1 = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        File file2 = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(file1, file2);

        // there should be 1 dataset for the default schema A
        assertLenientEquals(new String[] {
            "SCHEMA_A"
        }, result.getSchemaNames());

        // the dataset should contain 4 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        String[] actual = dataSet.getTableNames();
        assertLenientEquals(new String[] {
            "TABLE_A"
        }, actual);
        assertEquals(4, dataSet.getTable("TABLE_A").getRowCount());
    }
}
