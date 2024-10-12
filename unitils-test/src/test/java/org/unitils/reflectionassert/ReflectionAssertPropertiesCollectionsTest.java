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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionAssert} tests for with
 * assertProperty methods with collection arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertPropertiesCollectionsTest {
    /* A test collection */
    private List<TestObject> list;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        list = asList(new TestObject(1L, "el1"), new TestObject(2L, "el2"));
    }

    /**
     * Test for equal property values.
     */
    @Test
    public void testAssertPropertyReflectionEquals() {
        assertPropertyReflectionEquals("stringProperty", asList("el1", "el2"), list);
    }

    /**
     * Test for equal property values but of different types (int versus long).
     */
    @Test
    public void testAssertPropertyReflectionEquals_differentTypes() {
        assertPropertyReflectionEquals("primitiveProperty", asList(1L, 2L), list);
    }

    /**
     * Test for different property values.
     */
    @Test
    public void testAssertPropertyReflectionEquals_notEqualsDifferentValues() {
        AssertionError error = catchThrowableOfType(AssertionError.class,
            () -> assertPropertyReflectionEquals("stringProperty", asList("xxxxx", "xxxxx"), list));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test for property values with different order.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsDifferentOrder() {
        assertPropertyReflectionEquals("stringProperty", asList("el1", "el2"), list, LENIENT_ORDER);
    }

    /**
     * Test for property values with different order.
     */
    @Test
    public void testAssertPropertyLenientEquals_equalsDifferentOrder() {
        assertPropertyLenientEquals("stringProperty", asList("el1", "el2"), list);
    }

    /**
     * Test for property values with different order.
     */
    @Test
    public void testAssertPropertyReflectionEquals_notEqualsDifferentOrder() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyReflectionEquals("stringProperty", asList("el2", "el1"), list));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test for equal primitive property values. Using ints instead of longs.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsPrimitivesList() {
        assertPropertyLenientEquals("primitiveProperty", asList(2, 1), list);
    }

    /**
     * Test for different primitive property values. Using ints instead of longs.
     */
    @Test
    public void testAssertPropertyReflectionEquals_notEqualsPrimitivesList() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyLenientEquals("primitiveProperty", asList(999, 1), list));
        assertThat(error).as("Expected AssertionError").isNotNull();
    }

    /**
     * Test case for null as actual object argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_actualObjectNull() {
        AssertionError error = catchThrowableOfType(AssertionError.class, () -> assertPropertyLenientEquals("stringProperty", asList(1, 2), null));
        assertThat(error).as("Expected AssertionError").isNotNull();
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
