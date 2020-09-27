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

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

public class UnitilsJUnit5Extension
    implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, TestInstancePostProcessor,
    ExecutionCondition {
    protected TestListener unitilsTestListener;

    public UnitilsJUnit5Extension() {
        this.unitilsTestListener = getUnitilsTestListener();
    }

    @Override
    public void beforeAll(ExtensionContext context)
        throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        unitilsTestListener.beforeTestClass(testClass);
    }

    @Override
    public void beforeEach(ExtensionContext context)
        throws Exception {
        Object testObject = context.getRequiredTestInstance();
        Method testMethod = context.getRequiredTestMethod();
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
    }

    @Override
    public void afterEach(ExtensionContext context)
        throws Exception {
        Object testObject = context.getRequiredTestInstance();
        Method testMethod = context.getRequiredTestMethod();
        unitilsTestListener.afterTestTearDown(testObject, testMethod);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context)
        throws Exception {
        Object testObject = context.getRequiredTestInstance();
        Method testMethod = context.getRequiredTestMethod();
        unitilsTestListener.beforeTestMethod(testObject, testMethod);
    }

    @Override
    public void afterTestExecution(ExtensionContext context)
        throws Exception {
        Object testObject = context.getRequiredTestInstance();
        Method testMethod = context.getRequiredTestMethod();
        Throwable testThrowable = context.getExecutionException().orElse(null);
        unitilsTestListener.afterTestMethod(testObject, testMethod, testThrowable);
    }

    @Override
    public void postProcessTestInstance(Object testObject, ExtensionContext context)
        throws Exception {
        unitilsTestListener.afterCreateTestObject(testObject);
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Object testObject = context.getTestInstance().orElse(null);
        Method testMethod = context.getTestMethod().orElse(null);
        if (testObject == null || testMethod == null || unitilsTestListener.shouldInvokeTestMethod(testObject, testMethod)) {
            return ConditionEvaluationResult.enabled(null);
        } else {
            return ConditionEvaluationResult.disabled(null);
        }
    }

    protected TestListener getUnitilsTestListener() {
        return Unitils.getInstance().getTestListener();
    }
}
