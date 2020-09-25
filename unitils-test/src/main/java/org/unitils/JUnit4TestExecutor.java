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

import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class JUnit4TestExecutor
    implements TestExecutor {
    private Result result;

    public JUnit4TestExecutor() {
        super();
    }

    @Override
    public void runTests(Class<?>... testClasses)
        throws Exception {
        result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        for (Class<?> testClass : testClasses) {
            UnitilsBlockJUnit4ClassRunner testClassRunner = new UnitilsBlockJUnit4ClassRunner(testClass);
            testClassRunner.run(runNotifier);
        }
    }

    @Override
    public void runTests(String testGroup, Class<?>... testClasses)
        throws Exception {
        runTests(testClasses);
    }

    @Override
    public int getRunCount() {
        return result.getRunCount();
    }

    @Override
    public int getFailureCount() {
        return result.getFailureCount();
    }

    @Override
    public int getIgnoreCount() {
        return result.getIgnoreCount();
    }

    /**
     * Overridden test class runner to be able to use the {@link TracingTestListener} as test listener.
     */
    class TestUnitilsJUnit4TestClassRunner
        extends UnitilsBlockJUnit4ClassRunner {
        public TestUnitilsJUnit4TestClassRunner(Class<?> testClass)
            throws InitializationError {
            super(testClass);
        }
    }
}
