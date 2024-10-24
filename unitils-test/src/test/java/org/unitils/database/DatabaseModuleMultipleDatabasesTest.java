package org.unitils.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.reflectionassert.ReflectionAssert;

/**
 * Test multiple databases.
 * 
 * @author wiw
 * @since 3.4
 */
public class DatabaseModuleMultipleDatabasesTest {
    private Properties unitilsConfig;

    // @TestedObject
    private DatabaseModule module;

    @Before
    public void setUp()
        throws FileNotFoundException, IOException {
        module = new DatabaseModule();
        String strFile = FilenameUtils.separatorsToSystem("src\\test\\resources\\org\\unitils\\database\\config\\testconfigMultipleDatabases.properties");
        File file = new File(strFile);
        unitilsConfig = (Properties) Unitils.getInstance().getConfiguration().clone();
        unitilsConfig.load(new FileInputStream(file));
        module.init(unitilsConfig);
    }

    @Test
    public void testGetdefaultDatabaseWrapper() {
        DataSourceWrapper actual = module.getWrapper("");
        ReflectionAssert.assertLenientEquals(getWrapper1(), actual);
    }

    @Test
    public void testGetDatabase1()
        throws SQLException, SecurityException, IllegalArgumentException {
        TestClassDatabase1 obj = new TestClassDatabase1();
        DataSourceWrapper wrapper = module.getWrapper("");
        module.setWrapper(wrapper);
        module.injectDataSource(obj);

        Assert.assertNotNull(obj.dataSource);
        Assert.assertEquals("jdbc:hsqldb:mem:unitils1", obj.dataSource.getConnection().getMetaData().getURL());
    }

    @Test
    public void testGetDatabase2()
        throws SQLException, SecurityException, IllegalArgumentException {
        TestClassDatabase2 obj = new TestClassDatabase2();

        DataSourceWrapper wrapper = module.getWrapper("database2");
        module.setWrapper(wrapper);

        module.injectDataSource(obj);
        Assert.assertNotNull(obj.dataSource);
        Assert.assertEquals("jdbc:h2:~/test", obj.dataSource.getConnection().getMetaData().getURL());
    }

    @AfterClass
    public static void afterTestClass() {
        Unitils.getInstance().init();
    }

    private DataSourceWrapper getWrapper1() {
        DatabaseConfiguration conf = new DatabaseConfiguration("database1", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils1", "sa", null, "public",
            Arrays.asList("public"), false, true);
        return new DataSourceWrapper(conf, unitilsConfig, module.getTransactionManager());
    }

    private class TestClassDatabase1 {
        @TestDataSource("database1")
        private DataSource dataSource;

        @Test
        public void testMethod() {
            // do nothing
        }
    }

    private class TestClassDatabase2 {
        @TestDataSource("database2")
        private DataSource dataSource;

        @Test
        public void testMethod() {
            // do nothing
        }
    }
}
