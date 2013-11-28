/*
 * CVS file status:
 * 
 * $$Id: ResourceLoadingClassTest.java,v 1.2 2010-12-20 09:54:18 tdr Exp $$
 * 
 * Copyright (c) Smals
 */
package org.unitils.dbunit.locator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

/**
 * Unit test for simple App.
 * The test looks for xml files with the same name as the value of the {@link ResourceDataSet} on the classpath 
 * and gives the most recent.
 * There are 2 files with the same name: 
 * <ul>
 *  <li>in the unitilsmodules</li>
 *  <li>in the TestAppResources module</li>
 * </ul>
 * 
 * @author tdr
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet(value = "org/unitils/testdata/exampleResourceData.xml")
public class ResourceLoadingClassTest {

    /*** */
    @Test
    @ExpectedDataSet("org/unitils/testdata/testdata/exampleResourceData.xml")
    public void testLoadingResource() {
        //SqlAssert.assertCountSqlResult("select count(*) from dossier", 3L);
        //SqlAssert.assertMultipleRowSqlResult("select * from dossier", new String[]{"DS-1", "TestAppResourcesBlack"}, new String[]{"DS-2", "n"}, new String[]{"DS-3", "decker"});
        Assert.assertTrue(true);
    }   


}