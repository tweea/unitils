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

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.junit.AfterCreateTestObjectStatement;
import org.unitils.core.junit.AfterTestMethodStatement;
import org.unitils.core.junit.AfterTestTearDownStatement;
import org.unitils.core.junit.BeforeTestMethodStatement;
import org.unitils.core.junit.BeforeTestSetUpStatement;
import org.unitils.core.junit.ShouldInvokeTestMethodStatement;

public class UnitilsBlockJUnit4ClassRunnerWithParameters
    extends BlockJUnit4ClassRunnerWithParameters {
    protected Object test;

    protected TestListener unitilsTestListener;

    public UnitilsBlockJUnit4ClassRunnerWithParameters(TestWithParameters test)
        throws InitializationError {
        super(test);
        this.unitilsTestListener = getUnitilsTestListener();
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        this.test = test;

        Statement statement = super.methodInvoker(method, test);
        statement = new BeforeTestMethodStatement(unitilsTestListener, statement, test, method.getMethod());
        statement = new AfterTestMethodStatement(unitilsTestListener, statement, test, method.getMethod());
        return statement;
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Method testMethod = method.getMethod();

        Statement statement = super.methodBlock(method);
        statement = new BeforeTestSetUpStatement(unitilsTestListener, statement, test, testMethod);
        statement = new ShouldInvokeTestMethodStatement(unitilsTestListener, statement, test, testMethod);
        if (!isIgnored(method)) {
            statement = new AfterCreateTestObjectStatement(unitilsTestListener, statement, test);
        }
        statement = new AfterTestTearDownStatement(unitilsTestListener, statement, test, testMethod);
        return statement;
    }

    protected TestListener getUnitilsTestListener() {
        return Unitils.getInstance().getTestListener();
    }
}
