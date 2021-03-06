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
package org.unitils.dbmaintainer.clean.impl;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hsqldb.Trigger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.util.PropertyUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.core.util.SQLTestUtils.dropTestMaterializedViews;
import static org.unitils.core.util.SQLTestUtils.dropTestSequences;
import static org.unitils.core.util.SQLTestUtils.dropTestSynonyms;
import static org.unitils.core.util.SQLTestUtils.dropTestTables;
import static org.unitils.core.util.SQLTestUtils.dropTestTriggers;
import static org.unitils.core.util.SQLTestUtils.dropTestTypes;
import static org.unitils.core.util.SQLTestUtils.dropTestViews;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_VERSION_TABLE_NAME;

/**
 * Test class for the {@link DBClearer}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Scott Prater
 */
public class DefaultDBClearerTest
    extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Logger logger = LoggerFactory.getLogger(DefaultDBClearerTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DefaultDBClearer defaultDbClearer;

    /* The DbSupport object */
    private DbSupport dbSupport;

    /* The name of the version tabel */
    private String versionTableName;

    private static String dialect = "hsqldb";

    private List<String> schemas;

    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     */
    @Before
    public void setUp()
        throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        schemas = PropertyUtils.getStringList("database.schemaNames", configuration);
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        dbSupport = getDefaultDbSupport(configuration, sqlHandler, dialect, schemas.get(0));
        // create clearer instance
        defaultDbClearer = new DefaultDBClearer();
        defaultDbClearer.init(configuration, sqlHandler, dialect, schemas);
        versionTableName = configuration.getProperty(PROPKEY_VERSION_TABLE_NAME);

        cleanupTestDatabase();
        createTestDatabase();
    }

    /**
     * Removes all test tables.
     */
    @After
    public void tearDown()
        throws Exception {
        cleanupTestDatabase();
    }

    /**
     * Checks if the tables are correctly dropped.
     */
    @Test
    public void testClearDatabase_tables()
        throws Exception {
        assertEquals(2, dbSupport.getTableNames().size());
        defaultDbClearer.clearSchemas();
        assertTrue(dbSupport.getTableNames().isEmpty());
    }

    /**
     * Checks if the db version table is preserved.
     */
    @Test
    public void testClearDatabase_dbVersionTables()
        throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        assertEquals(3, dbSupport.getTableNames().size());
        defaultDbClearer.clearSchemas();
        assertEquals(1, dbSupport.getTableNames().size()); // version table
    }

    /**
     * Checks if the views are correctly dropped
     */
    @Test
    public void testClearDatabase_views()
        throws Exception {
        assertEquals(2, dbSupport.getViewNames().size());
        defaultDbClearer.clearSchemas();
        assertTrue(dbSupport.getViewNames().isEmpty());
    }

    /**
     * Checks if the materialized views are correctly dropped
     */
    @Test
    public void testClearDatabase_materializedViews()
        throws Exception {
        if (!dbSupport.supportsMaterializedViews()) {
            logger.warn("Current dialect does not support materialized views. Skipping test.");
            return;
        }
        assertEquals(2, dbSupport.getMaterializedViewNames().size());
        defaultDbClearer.clearSchemas();
        assertTrue(dbSupport.getMaterializedViewNames().isEmpty());
    }

    /**
     * Checks if the synonyms are correctly dropped
     */
    @Test
    public void testClearDatabase_synonyms()
        throws Exception {
        if (!dbSupport.supportsSynonyms()) {
            logger.warn("Current dialect does not support synonyms. Skipping test.");
            return;
        }
        assertEquals(2, dbSupport.getSynonymNames().size());
        defaultDbClearer.clearSchemas();
        assertTrue(dbSupport.getSynonymNames().isEmpty());
    }

    /**
     * Tests if the triggers are correctly dropped
     */
    @Test
    public void testClearDatabase_sequences()
        throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertEquals(2, dbSupport.getSequenceNames().size());
        defaultDbClearer.clearSchemas();
        assertTrue(dbSupport.getSequenceNames().isEmpty());
    }

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase()
        throws Exception {
        String dialect = dbSupport.getDatabaseDialect();
        if ("hsqldb".equals(dialect)) {
            createTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            createTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            createTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            createTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            createTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            createTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            createTestDatabaseMsSql();
        } else {
            fail("This test is not implemented for current dialect: " + dialect);
        }
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabase()
        throws Exception {
        String dialect = dbSupport.getDatabaseDialect();
        if ("hsqldb".equals(dialect)) {
            cleanupTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            cleanupTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            cleanupTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            cleanupTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            cleanupTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            cleanupTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            cleanupTestDatabaseMsSql();
        }
    }

    //
    // Database setup for HsqlDb
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseHsqlDb()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"",
            dataSource);
        executeUpdate(
            "create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"",
            dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseHsqlDb()
        throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
    }

    /**
     * Test trigger for hypersonic.
     *
     * @author Filip Neven
     * @author Tim Ducheyne
     */
    public static class TestTrigger
        implements Trigger {
        @Override
        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }

    //
    // Database setup for MySql
    //

    /**
     * Creates all test database structures (view, tables...)
     * <p/>
     * NO FOREIGN KEY USED: drop cascade does not work in
     * MySQL
     */
    private void createTestDatabaseMySql()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int)", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseMySql()
        throws Exception {
        dropTestTables(dbSupport, "test_table", "`Test_CASE_Table`", versionTableName);
        dropTestViews(dbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(dbSupport, "test_trigger", "`Test_CASE_Trigger`");
    }

    //
    // Database setup for Oracle
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseOracle()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create materialized views
        executeUpdate("create materialized view test_mview as select col1 from test_table", dataSource);
        executeUpdate("create materialized view \"Test_CASE_MView\" as select col1 from test_table", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create or replace trigger test_trigger before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end test_trigger",
            dataSource);
        executeUpdate(
            "create or replace trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end \"Test_CASE_Trigger\"",
            dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseOracle()
        throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestMaterializedViews(dbSupport, "test_mview", "\"Test_CASE_MView\"");
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for PostgreSql
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabasePostgreSql()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        try {
            executeUpdate("create language plpgsql", dataSource);
        } catch (Exception e) {
            logger.trace("", e);
            // ignore language already exists
        }
        executeUpdate("create or replace function test() returns trigger as $$ declare begin end; $$ language plpgsql", dataSource);
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabasePostgreSql()
        throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Db2
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseDb2()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int) MODE DB2SQL", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int) MODE DB2SQL", dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseDb2()
        throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Derby
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseDerby()
        throws Exception {
        // create tables
        executeUpdate("create table \"TEST_TABLE\" (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("call SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('testKey', 'test')", dataSource);
        executeUpdate(
            "create trigger test_trigger no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')",
            dataSource);
        executeUpdate(
            "create trigger \"Test_CASE_Trigger\" no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')",
            dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...) First drop the views, since Derby doesn't support
     * "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseDerby()
        throws Exception {
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(dbSupport, "\"Test_CASE_Table\"", "TEST_TABLE", versionTableName);
    }

    //
    // Database setup for MS-Sql
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseMsSql()
        throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        // create types
        executeUpdate("create type test_type from int", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" from int", dataSource);
    }

    /**
     * Drops all created test database structures (views, tables...) First drop the views, since Derby doesn't support
     * "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseMsSql()
        throws Exception {
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(dbSupport, "\"Test_CASE_Table\"", "test_table", versionTableName);
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }
}
