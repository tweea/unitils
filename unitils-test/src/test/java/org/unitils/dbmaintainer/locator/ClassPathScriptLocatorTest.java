package org.unitils.dbmaintainer.locator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.ResourcePickingStrategie;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

/**
 * Will test finding scripts on the classpath.
 * 
 * @author tdr
 * @since 1.0.2
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class ClassPathScriptLocatorTest {
    @TestedObject
    private ClassPathScriptLocator classPathScriptLocator;

    @Mock
    private ResourcePickingStrategie resourcePickingStrategie;

    private Properties configuration;

    private List<URL> urlList;

    private String scriptDir = "/org/unitils/dbunit/testdbscripts/";

    @Before
    public void setUp()
        throws Exception {
        urlList = new ArrayList<>();
        urlList.add(getClass().getResource(scriptDir + "001_Initial_TESTcreate.sql"));
        urlList.add(getClass().getResource(scriptDir + "002_Initial_TESTcreate.sql"));
        urlList.add(getClass().getResource(scriptDir + "003_Initial_TESTcreate.sql"));
        Assert.assertFalse("Some of the needed resources is not found", urlList.contains(null));
        configuration = (Properties) Unitils.getInstance().getConfiguration().clone();
    }

    @AfterClass
    public static void afterTestClass() {
        Unitils.getInstance().init();
    }

    /*** */
    @Test
    public void loadScriptsTest() {
        String path = "/org/unitils/dbunit/testdbscripts/";

        List<Script> scriptList = new ArrayList<>();

        List<String> scriptExtensions = new ArrayList<>();
        scriptExtensions.add(".sql");

        EasyMock.expect(resourcePickingStrategie.filter((List<URL>) EasyMock.anyObject(), (String) EasyMock.anyObject())).andReturn(urlList);

        EasyMockUnitils.replay();
        classPathScriptLocator = new ClassPathScriptLocator();
        classPathScriptLocator.loadScripts(scriptList, path, resourcePickingStrategie, scriptExtensions, "users", true, configuration);

        Assert.assertTrue(classPathScriptLocator.scriptList.size() == 3);
    }
}
