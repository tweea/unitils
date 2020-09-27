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
package org.unitils;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.core.annotation.UsedForTesting;

import static org.unitils.TracingTestListener.TestInvocation.TEST_AFTER_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_BEFORE_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

/**
 * JUnit 4 test class containing 2 active and 1 ignored test method. This test test-class is used
 * in the JUnitUnitilsInvocationTest and JUnitUnitilsInvocationExceptionTest tests.
 *
 * @author Tim Ducheyne
 */
public class UnitilsJUnit4ParameterizedTest_TestClass1
    extends UnitilsJUnit4ParameterizedTestBase {
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
            {
                1
            }
        };
        return Arrays.asList(data);
    }

    @UsedForTesting
    private int number;

    public UnitilsJUnit4ParameterizedTest_TestClass1(int number) {
        this.number = number;
    }

    @BeforeClass
    public static void beforeClass() {
        registerTestInvocation(TEST_BEFORE_CLASS, UnitilsJUnit4ParameterizedTest_TestClass1.class, null);
    }

    @AfterClass
    public static void afterClass() {
        registerTestInvocation(TEST_AFTER_CLASS, UnitilsJUnit4ParameterizedTest_TestClass1.class, null);
    }

    @Before
    public void setUp() {
        registerTestInvocation(TEST_SET_UP, this.getClass(), null);
    }

    @After
    public void tearDown() {
        registerTestInvocation(TEST_TEAR_DOWN, this.getClass(), null);
    }

    @Test
    public void test1() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test1");
    }

    @Test
    public void test2() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test2");
    }

    @Ignore
    @Test
    public void test3() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test3");
    }
}
