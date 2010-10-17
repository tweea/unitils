/*
 * Copyright Unitils.org
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
package org.unitils.dbmaintainer.clean.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertEquals;
import static org.unitils.database.DatabaseUnitils.clearDatabase;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.testutil.TestUnitilsConfiguration.*;

/**
 * Test class for the clearing the database using multiple database schema's with configuration to preserve all items. <p/>
 * This test is currently only implemented for HsqlDb
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearerMultiSchemaPreserveTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerMultiSchemaPreserveTest.class);

    private DataSource dataSource;
    private Database defaultDatabase;
    private String versionTableName;
    private boolean disabled;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // configure 3 schemas
        configuration.setProperty(PROPERTY_SCHEMANAMES, "PUBLIC, SCHEMA_A, \"SCHEMA_B\", schema_c");

        // configure items to preserve
        defaultDatabase = getDefaultDatabase();
        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");
        configuration.setProperty(PROPERTY_PRESERVE_SCHEMAS, "schema_c");
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, "test_table, " + defaultDatabase.quoted("SCHEMA_A") + "." + defaultDatabase.quoted("TEST_TABLE"));
        configuration.setProperty(PROPERTY_PRESERVE_VIEWS, "test_view, " + "schema_a." + defaultDatabase.quoted("TEST_VIEW"));
        configuration.setProperty(PROPERTY_PRESERVE_SEQUENCES, "test_sequence, " + defaultDatabase.quoted("SCHEMA_A") + ".test_sequence");

        reinitializeUnitils(configuration);
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);
        defaultDatabase = getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();

        cleanupTestDatabase();
        createTestDatabase();
    }

    @After
    public void cleanUp() throws Exception {
        if (disabled) {
            return;
        }
        resetUnitils();
        cleanupTestDatabase();
    }


    @Test
    public void clearTables() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getTableNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_C").size());
        clearDatabase();
        assertEquals(2, defaultDatabase.getTableNames("PUBLIC").size()); // version table was created
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_A").size());
        assertEquals(0, defaultDatabase.getTableNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_C").size());
    }

    @Test
    public void clearViews() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getViewNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_C").size());
        clearDatabase();
        assertEquals(1, defaultDatabase.getViewNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_A").size());
        assertEquals(0, defaultDatabase.getViewNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_C").size());
    }

    @Test
    public void clearSequences() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getSequenceNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_C").size());
        clearDatabase();
        assertEquals(1, defaultDatabase.getSequenceNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_A").size());
        assertEquals(0, defaultDatabase.getSequenceNames("SCHEMA_B").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_C").size());
    }


    private void createTestDatabase() throws Exception {
        // create schemas
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        executeUpdate("create schema SCHEMA_C AUTHORIZATION DBA", dataSource);
        // create tables
        executeUpdate("create table TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_A.TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_B.TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_C.TEST_TABLE (col1 varchar(100))", dataSource);
        // create views
        executeUpdate("create view TEST_VIEW as select col1 from TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_A.TEST_VIEW as select col1 from SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_B.TEST_VIEW as select col1 from SCHEMA_B.TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_C.TEST_VIEW as select col1 from SCHEMA_C.TEST_TABLE", dataSource);
        // create sequences
        executeUpdate("create sequence TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_C.TEST_SEQUENCE", dataSource);
    }

    private void cleanupTestDatabase() throws Exception {
        // drop sequences
        executeUpdateQuietly("drop sequence TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_C.TEST_SEQUENCE", dataSource);
        // drop views
        executeUpdateQuietly("drop view TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_A.TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_B.TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_C.TEST_VIEW", dataSource);
        // drop tables
        executeUpdateQuietly("drop table " + versionTableName, dataSource);
        executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_C.TEST_TABLE", dataSource);
        // drop schemas
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_C", dataSource);
    }

}