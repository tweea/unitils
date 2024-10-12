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
package org.unitils.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.util.PropertyUtils.getBoolean;
import static org.unitils.util.PropertyUtils.getInstance;
import static org.unitils.util.PropertyUtils.getLong;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.PropertyUtils.getStringList;

/**
 * Test for {@link PropertyUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class PropertyUtilsTest {
    /* A test properties instance */
    private Properties testProperties;

    /**
     * Sets up the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        testProperties = new Properties();
        testProperties.setProperty("stringProperty", "test");
        testProperties.setProperty("stringListProperty", "test1, test2, test3 , ,");
        testProperties.setProperty("booleanProperty", "true");
        testProperties.setProperty("longProperty", "5");
        testProperties.setProperty("instanceProperty", "java.lang.StringBuffer");
        testProperties.setProperty("expandProperty", "A ${stringProperty} value");
        testProperties.setProperty("expandSystemProperty", "${user.home}");
    }

    /**
     * Test for getting a string property
     */
    @Test
    public void testGetString() {
        String result = getString("stringProperty", testProperties);
        assertEquals("test", result);
    }

    /**
     * Test for getting an unknown string property
     */
    @Test
    public void testGetString_notFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getString("xxxx", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a string property passing a default
     */
    @Test
    public void testGetString_default() {
        String result = getString("stringProperty", "default", testProperties);
        assertEquals("test", result);
    }

    /**
     * Test for getting an unknown string property passing a default
     */
    @Test
    public void testGetString_defaultNotFound() {
        String result = getString("xxxx", "default", testProperties);
        assertEquals("default", result);
    }

    /**
     * Test for getting a string list property
     */
    @Test
    public void testGetStringList() {
        List<String> result = getStringList("stringListProperty", testProperties);
        assertLenientEquals(asList("test1", "test2", "test3", ""), result);
    }

    /**
     * Test for getting an unknown string list property
     */
    @Test
    public void testGetStringList_notFound() {
        List<String> result = getStringList("xxxx", testProperties);
        assertTrue(result.isEmpty());
    }

    /**
     * Test for getting an unknown string list property
     */
    @Test
    public void testGetStringList_requiredNotFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getStringList("xxxx", testProperties, true));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a boolean property
     */
    @Test
    public void testGetBoolean() {
        boolean result = getBoolean("booleanProperty", testProperties);
        assertTrue(result);
    }

    /**
     * Test for getting an unknown boolean property
     */
    @Test
    public void testGetBoolean_notFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getBoolean("xxxx", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a boolean property passing a default
     */
    @Test
    public void testGetBoolean_default() {
        boolean result = getBoolean("booleanProperty", false, testProperties);
        assertTrue(result);
    }

    /**
     * Test for getting an unknown boolean property passing a default
     */
    @Test
    public void testGetBoolean_defaultNotFound() {
        boolean result = getBoolean("xxxx", false, testProperties);
        assertFalse(result);
    }

    /**
     * Test for getting a long property
     */
    @Test
    public void testGetLong() {
        long result = getLong("longProperty", testProperties);
        assertEquals(5, result);
    }

    /**
     * Test for getting a long property that is not a number
     */
    @Test
    public void testGetLong_notNumber() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getLong("stringProperty", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting an unknown long property
     */
    @Test
    public void testGetLong_notFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getLong("xxxx", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a long property passing a default
     */
    @Test
    public void testGetLong_default() {
        long result = getLong("longProperty", 10, testProperties);
        assertEquals(5, result);
    }

    /**
     * Test for getting a long property that is not a number passing a default
     */
    @Test
    public void testGetLong_defaultNotNumber() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getLong("stringProperty", 10, testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting an unknown long property passing a default
     */
    @Test
    public void testGetLong_defaultNotFound() {
        long result = getLong("xxxx", 10, testProperties);
        assertEquals(10, result);
    }

    /**
     * Test for getting an object instance.
     */
    @Test
    public void testGetInstance() {
        Object result = getInstance("instanceProperty", testProperties);
        assertTrue(result instanceof StringBuffer);
    }

    /**
     * Test for getting an unknown object instance property
     */
    @Test
    public void testGetInstance_notFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getInstance("xxxx", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a object instance property that does not contain a class name
     */
    @Test
    public void testGetInstance_couldNotCreate() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getInstance("stringProperty", testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test for getting a object instance property passing a default
     */
    @Test
    public void testGetInstance_default() {
        Object result = getInstance("instanceProperty", new ArrayList<>(), testProperties);
        assertTrue(result instanceof StringBuffer);
    }

    /**
     * Test for getting an unknown object instance property passing a default
     */
    @Test
    public void testGetInstance_defaultNotFound() {
        Object result = getInstance("xxxx", new ArrayList<>(), testProperties);
        assertTrue(result instanceof ArrayList);
    }

    /**
     * Test for getting a object instance property that does not contain a class name passing a default
     */
    @Test
    public void testGetInstance_defaultCouldNotCreate() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getInstance("stringProperty", new ArrayList<>(), testProperties));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }
}
