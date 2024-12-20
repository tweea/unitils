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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.core.annotation.UsedForTesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.unitils.util.CollectionUtils.asSet;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.getFieldValue;
import static org.unitils.util.ReflectionUtils.getGetter;
import static org.unitils.util.ReflectionUtils.getPropertyName;
import static org.unitils.util.ReflectionUtils.getSimpleMethodName;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;
import static org.unitils.util.ReflectionUtils.setFieldValue;

/**
 * Test for {@link ReflectionUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtilsTest {
    /* A test object instance */
    private TestObject testObject;

    /* A field in the test object */
    private Field field;

    /* A setter method in the test object */
    private Method fieldSetterMethod;

    /**
     * Sets up the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        testObject = new TestObject();
        field = TestObject.class.getDeclaredField("field");
        fieldSetterMethod = TestObject.class.getDeclaredMethod("setField", String.class);
    }

    /**
     * Test for creating a class instance.
     */
    @Test
    public void testCreateInstanceOfType() {
        String result = (String) createInstanceOfType("java.lang.String", false);
        assertNotNull(result);
    }

    /**
     * Test for creating a class instance, but with an unexisting class name.
     */
    @Test
    public void testCreateInstanceOfType_classNotFound() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> createInstanceOfType("xxxxxx", false));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for getting the value of a field.
     */
    @Test
    public void testGetFieldValue() {
        Object result = getFieldValue(testObject, field);
        assertEquals("testValue", result);
    }

    /**
     * Test for getting the value of a field that is not of the test object.
     */
    @Test
    public void testGetFieldValue_unexistingField()
        throws Exception {
        // get another field
        Field anotherField = getClass().getDeclaredField("testObject");
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getFieldValue(testObject, anotherField));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for setting the value of a field.
     */
    @Test
    public void testSetFieldValue() {
        setFieldValue(testObject, field, "newValue");
        assertEquals("newValue", testObject.getField());
    }

    /**
     * Test for setting the value of a field. Null value.
     */
    @Test
    public void testSetFieldValue_null() {
        setFieldValue(testObject, field, null);
        assertNull(testObject.getField());
    }

    /**
     * Test for setting the value of a field that is not of the test object.
     */
    @Test
    public void testSetFieldValue_unexistingField()
        throws Exception {
        // get another field
        Field anotherField = getClass().getDeclaredField("testObject");

        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> setFieldValue(testObject, anotherField, "newValue"));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for setting the value of a field and setter.
     */
    @Test
    public void testSetFieldAndSetterValue_field() {
        setFieldAndSetterValue(testObject, asSet(field), new HashSet<>(), "newValue");
        assertEquals("newValue", testObject.getField());
    }

    /**
     * Test for setting the value of a field and setter.
     */
    @Test
    public void testSetFieldAndSetterValue_setter() {
        setFieldAndSetterValue(testObject, new HashSet<>(), asSet(fieldSetterMethod), "newValue");
        assertEquals("newValue", testObject.getField());
    }

    /**
     * Test for setting the value of a field and setter. Null value
     */
    @Test
    public void testSetFieldAndSetterValue_null() {
        setFieldAndSetterValue(testObject, asSet(field), asSet(fieldSetterMethod), null);
        assertNull(testObject.getField());
    }

    /**
     * Test for setting the value of a field and setter. Field not found
     */
    @Test
    public void testSetFieldAndSetterValue_unexistingField()
        throws Exception {
        // get another field
        Field anotherField = getClass().getDeclaredField("testObject");
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> setFieldAndSetterValue(testObject, asSet(anotherField), asSet(fieldSetterMethod), "newValue"));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for setting the value of a field and setter. Method not found
     */
    @Test
    public void testSetFieldAndSetterValue_unexistingMethod()
        throws Exception {
        // get another field
        Method anotherMethod = getClass().getDeclaredMethod("testInvokeMethod_unexistingMethod");
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> setFieldAndSetterValue(testObject, asSet(field), asSet(anotherMethod), "newValue"));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for setting the value of a field that is of a wrong type.
     */
    @Test
    public void testSetFieldValue_wrongType()
        throws Exception {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> setFieldValue(testObject, field, 0));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for performing a method invocation.
     */
    @Test
    public void testInvokeMethod()
        throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, "newValue");
        assertNull(result);
        assertEquals("newValue", testObject.getField());
    }

    /**
     * Test for performing a method invocation. Null value
     */
    @Test
    public void testInvokeMethod_null()
        throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, (Object) null);
        assertNull(result);
        assertNull(testObject.getField());
    }

    /**
     * Test for performing a method invocation of a method that is not of the test object.
     */
    @Test
    public void testInvokeMethod_unexistingMethod()
        throws Exception {
        // get another method
        Method anotherMethod = getClass().getDeclaredMethod("testInvokeMethod_unexistingMethod");
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> invokeMethod(testObject, anotherMethod));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for performing a method invocation of a field that is of a wrong type.
     */
    @Test
    public void testInvokeMethod_wrongType()
        throws Exception {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> invokeMethod(testObject, fieldSetterMethod, 0));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Test for performing a method invocation. Null value
     */
    @Test
    public void testGetFieldName()
        throws Exception {
        String result = getPropertyName(fieldSetterMethod);
        assertEquals("field", result);
    }

    /**
     * Test for performing a method invocation. Null value
     */
    @Test
    public void testGetFieldName_noSetter()
        throws Exception {
        Method anotherMethod = getClass().getDeclaredMethod("testGetFieldName_noSetter");
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> getPropertyName(anotherMethod));
        assertThat(exception).as("UnitilsException expected").isNotNull();
    }

    /**
     * Tests creating a represenation of a method name.
     */
    @Test
    public void testGetSimpleMethodName() {
        String result = getSimpleMethodName(fieldSetterMethod);
        assertEquals("TestObject.setField()", result);
    }

    @Test
    public void testGetGetter() {
        Method result = getGetter(TestObject.class, "field", false);
        assertEquals("getField", result.getName());
    }

    @Test
    public void testGetGetterBooleanVersion() {
        Method result = getGetter(TestObject.class, "boolField", false);
        assertEquals("isBoolField", result.getName());
    }

    /**
     * A test object containing a private field.
     */
    private static class TestObject {
        private String field = "testValue";

        private boolean boolField = true;

        public String getField() {
            return field;
        }

        @UsedForTesting
        public void setField(String field) {
            this.field = field;
        }

        @UsedForTesting
        public boolean isBoolField() {
            return boolField;
        }
    }
}
