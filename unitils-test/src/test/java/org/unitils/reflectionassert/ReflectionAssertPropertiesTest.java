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
package org.unitils.reflectionassert;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertPropertiesTest {
    /* Test object */
    private TestObject testObject;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        testObject = new TestObject(1, "stringValue");
    }

    /**
     * Test for equal property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equals() {
        assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
    }

    /**
     * Test for equal property value (message version).
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsMessage() {
        assertPropertyReflectionEquals("a message", "stringProperty", "stringValue", testObject);
    }

    /**
     * Test for equal property value.
     */
    @Test
    public void testAssertPropertyLenientEquals_equals() {
        assertPropertyLenientEquals("stringProperty", "stringValue", testObject);
    }

    /**
     * Test for equal property value (message version).
     */
    @Test
    public void testAssertPropertyLenientEquals_equalsMessage() {
        assertPropertyLenientEquals("a message", "stringProperty", "stringValue", testObject);
    }

    /**
     * Test for equal primitive property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsPrimitive() {
        assertPropertyReflectionEquals("primitiveProperty", 1L, testObject);
    }

    /**
     * Test for different property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_notEqualsDifferentValues() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyReflectionEquals("stringProperty", "xxxxxx", testObject));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test case for a null left-argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_leftNull() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyReflectionEquals("stringProperty", null, testObject));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test case for a null right-argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_rightNull() {
        testObject.setStringProperty(null);
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyReflectionEquals("stringProperty", "stringValue", testObject));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test case for null as actual object argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_actualObjectNull() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyReflectionEquals("aProperty", "aValue", null));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test case for both null arguments.
     */
    @Test
    public void testAssertPropertyReflectionEquals_null() {
        testObject.setStringProperty(null);
        assertPropertyReflectionEquals("stringProperty", null, testObject);
    }

    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsIgnoredDefault() {
        assertPropertyReflectionEquals("a message", "stringProperty", null, testObject, IGNORE_DEFAULTS);
    }

    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertPropertyLenientEquals_equalsIgnoredDefault() {
        assertPropertyLenientEquals("stringProperty", null, testObject);
    }

    /**
     * Test class with failing equals containing test properties.
     */
    public class TestObject {
        private long primitiveProperty;

        private String stringProperty;

        public TestObject(long primitiveProperty, String stringProperty) {
            this.primitiveProperty = primitiveProperty;
            this.stringProperty = stringProperty;
        }

        public long getPrimitiveProperty() {
            return primitiveProperty;
        }

        public void setPrimitiveProperty(long primitiveProperty) {
            this.primitiveProperty = primitiveProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }
}
