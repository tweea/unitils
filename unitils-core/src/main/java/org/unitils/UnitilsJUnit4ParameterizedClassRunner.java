/*
 * Copyright 2013, Unitils.org
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

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.junit.BeforeTestClassStatement;

public class UnitilsJUnit4ParameterizedClassRunner
    extends Parameterized {
    protected TestListener unitilsTestListener;

    public UnitilsJUnit4ParameterizedClassRunner(Class<?> testClass)
        throws Throwable {
        super(testClass);
        this.unitilsTestListener = getUnitilsTestListener();
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Class<?> testClass = getTestClass().getJavaClass();

        Statement statement = super.classBlock(notifier);
        statement = new BeforeTestClassStatement(unitilsTestListener, statement, testClass);
        // statement = new AfterTestClassStatement(unitilsTestListener, statement);
        return statement;
    }

    protected TestListener getUnitilsTestListener() {
        return Unitils.getInstance().getTestListener();
    }
}
