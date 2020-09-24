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

import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertSame;

/**
 * Test for {@link ReflectionUtils} that use enumeration values.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsEnumTest {
    /**
     * Test for get enum value.
     */
    @Test
    public void testGetEnumValue() {
        TestEnum result = ReflectionUtils.getEnumValue(TestEnum.class, "VALUE1");
        assertSame(TestEnum.VALUE1, result);
    }

    /**
     * Test for get enum value with different case.
     */
    @Test
    public void testGetEnumValue_differentCase() {
        TestEnum result = ReflectionUtils.getEnumValue(TestEnum.class, "Value1");
        assertSame(TestEnum.VALUE1, result);
    }

    /**
     * Test for get enum value, but an unknown value name.
     * Should fail with a runtime exception.
     */
    @Test
    public void testGetEnumValue_unexisting() {
        UnitilsException exception = catchThrowableOfType(() -> ReflectionUtils.getEnumValue(TestEnum.class, "xxxxxxxxx"), UnitilsException.class);
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test enumeration with a default value.
     */
    private enum TestEnum {
        DEFAULT, VALUE1, VALUE2
    }
}
