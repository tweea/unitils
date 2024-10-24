/*
 * Copyright 2011, Unitils.org
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
package org.unitils.core.config;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetLongTest {
    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;

    @Before
    public void initialize()
        throws Exception {
        Properties properties = new Properties();
        properties.setProperty("longProperty", "5");
        properties.setProperty("valueWithSpaces", "  5  ");
        properties.setProperty("invalidLongValue", "xxx");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }

    @Test
    public void foundWithoutDefault() {
        long result = unitilsConfiguration.getLong("longProperty");
        assertEquals(5L, result);
    }

    @Test
    public void foundWithDefault() {
        long result = unitilsConfiguration.getLong("longProperty", 6);
        assertEquals(5L, result);
    }

    @Test
    public void valueIsTrimmedWithoutDefault() {
        long result = unitilsConfiguration.getLong("valueWithSpaces");
        assertEquals(5L, result);
    }

    @Test
    public void valueIsTrimmedWithDefault() {
        long result = unitilsConfiguration.getLong("valueWithSpaces", 6L);
        assertEquals(5L, result);
    }

    @Test
    public void notFoundNoDefault() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> unitilsConfiguration.getLong("xxx"));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    @Test
    public void notFoundWithDefault() {
        long result = unitilsConfiguration.getLong("xxx", 6L);
        assertEquals(6L, result);
    }

    @Test
    public void invalidLongValueWithoutDefault() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> unitilsConfiguration.getLong("invalidLongValue"));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    @Test
    public void invalidLongValueWithDefault() {
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> unitilsConfiguration.getLong("invalidLongValue", 6L));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }
}
