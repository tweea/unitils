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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.TracingTestListener.TestFramework;

import static org.unitils.TracingTestListener.TestFramework.JUNIT4;

/**
 * Test for the main flow of the unitils test listeners for
 * JUnit4 (@link UnitilsBlockJUnit4ClassRunner}) and TestNG.
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
 * that does not contain any methods. TestClass1 also contains an ignored test.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass2
 */
@RunWith(Parameterized.class)
public class JUnitUnitilsInvocationTest
    extends UnitilsInvocationTestBase {
    private Class<?> testClass1, testClass2;

    public JUnitUnitilsInvocationTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass1, Class<?> testClass2) {
        super(testFramework, testExecutor);
        this.testClass1 = testClass1;
        this.testClass2 = testClass2;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
            {
                JUNIT4, new JUnit4TestExecutor(), UnitilsJUnit4Test_TestClass1.class, UnitilsJUnit4Test_TestClass2.class
            }, {
                JUNIT4, new JUnit4ParameterizedTestExecutor(), UnitilsJUnit4ParameterizedTest_TestClass1.class, UnitilsJUnit4ParameterizedTest_TestClass2.class
            },
            // {JUNIT4, new JUnit4TestExecutor(), SpringUnitilsJUnit4Test_TestClass1.class, SpringUnitilsJUnit4Test_TestClass2.class},
        });
    }

    @Test
    public void testSuccessfulRun()
        throws Throwable {
        testExecutor.runTests(testClass1, testClass2);
        assertInvocationOrder(testClass1, testClass2);
    }
}
