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

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

public class JUnit5TestExecutor
    implements TestExecutor {
    private Result result;

    private int testClassNumber;

    @Override
    public void runTests(Class<?>... testClasses)
        throws Throwable {
        result = new Result();
        testClassNumber = testClasses.length;
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        for (Class<?> testClass : testClasses) {
            JUnitPlatform testClassRunner = new JUnitPlatform(testClass);
            testClassRunner.run(runNotifier);
        }
    }

    @Override
    public void runTests(String testGroup, Class<?>... testClasses)
        throws Throwable {
        runTests(testClasses);
    }

    @Override
    public int getRunCount() {
        return result.getRunCount() - testClassNumber;
    }

    @Override
    public int getFailureCount() {
        return result.getFailureCount();
    }

    @Override
    public int getIgnoreCount() {
        return result.getIgnoreCount();
    }
}
