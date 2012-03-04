/*
 * Copyright 2012,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitilsnew;

import org.junit.internal.runners.*;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.unitilsnew.core.Context;
import org.unitilsnew.core.config.BootstrapContextFactory;
import org.unitilsnew.core.listener.impl.UnitilsTestListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom test runner that will Unitils-enable your test. This will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link org.unitils.core.TestListener} for
 * more information on the listener invocation order.
 * <p/>
 * NOTE: if a test fails, the error is logged as debug logging. This is a temporary work-around for
 * a problem with IntelliJ JUnit-4 runner that reports a 'Wrong test finished' error when something went wrong
 * in the before. [IDEA-12498]
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsJUnit4TestClassRunner extends JUnit4ClassRunner {

    protected UnitilsTestListener unitilsTestListener;


    /**
     * Creates a test runner that runs all test methods in the given class.
     *
     * @param testClass the class, not null
     * @throws org.junit.internal.runners.InitializationError
     *
     */
    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }


    @Override
    public void run(final RunNotifier notifier) {
        ClassRoadie classRoadie = new ClassRoadie(notifier, getTestClass(), getDescription(), new Runnable() {
            public void run() {
                runMethods(notifier);
            }
        });

        try {
            getUnitilsTestListener().beforeTestClass(getTestClass().getJavaClass());
            classRoadie.runProtected();
        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
        }
    }


    /**
     * Overridden JUnit4 method to be able to create a CustomMethodRoadie that will invoke the
     * unitils test listener methods at the appropriate moments.
     */
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        Description description = methodDescription(method);
        Object testObject;
        try {
            testObject = createTest();
        } catch (InvocationTargetException e) {
            notifier.testAborted(description, e.getCause());
            return;
        } catch (Exception e) {
            notifier.testAborted(description, e);
            return;
        }
        TestMethod testMethod = wrapMethod(method);
        createMethodRoadie(testObject, method, testMethod, notifier, description).run();
    }


    /**
     * Returns the JUnit 4 MethodRoadie object that is used to execute the test method
     *
     * @param testObject      The test instance, not null
     * @param testMethod      The test method, not null
     * @param jUnitTestMethod The JUnit test method
     * @param notifier        The run listener, not null
     * @param description     A test description
     * @return An JUnit MethodRoadie
     */
    protected MethodRoadie createMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
        return new TestListenerInvokingMethodRoadie(testObject, testMethod, jUnitTestMethod, notifier, description);
    }


    /**
     * Custom method roadie that invokes the unitils test listener methods at the apropriate moments.
     */
    protected class TestListenerInvokingMethodRoadie extends MethodRoadie {


        /* Instance under test */
        protected Object testObject;

        /* Method under test */
        protected Method testMethod;

        protected Throwable throwable;


        /**
         * Creates a method roadie.
         *
         * @param testObject      The test instance, not null
         * @param testMethod      The test method, not null
         * @param jUnitTestMethod The JUnit test method
         * @param notifier        The run listener, not null
         * @param description     A test description
         */
        public TestListenerInvokingMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
            super(testObject, jUnitTestMethod, notifier, description);
            this.testObject = testObject;
            this.testMethod = testMethod;
        }


        /**
         * Overriden JUnit4 method to be able to call {@link org.unitils.core.TestListener#afterTestTearDown}.
         */
        @Override
        public void runBeforesThenTestThenAfters(Runnable test) {
            try {
                getUnitilsTestListener().beforeTestSetUp(testObject, testMethod);
            } catch (Throwable t) {
                addFailure(t);
            }
            if (throwable == null) {
                super.runBeforesThenTestThenAfters(test);
            }
            try {
                getUnitilsTestListener().afterTestTearDown();
            } catch (Throwable t) {
                addFailure(t);
            }
        }


        @Override
        protected void runTestMethod() {
            try {
                getUnitilsTestListener().beforeTestMethod();
            } catch (Throwable t) {
                addFailure(t);
            }
            if (throwable == null) {
                super.runTestMethod();
            }
            try {
                getUnitilsTestListener().afterTestMethod(throwable);
            } catch (Throwable t) {
                addFailure(t);
            }
        }


        /**
         * Registers a test failure
         *
         * @param t The exception, not null
         */
        @Override
        protected void addFailure(Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (throwable == null) {
                throwable = t;
                super.addFailure(t);
            }
        }
    }


    /**
     * @return The unitils test listener
     */
    protected UnitilsTestListener getUnitilsTestListener() {
        if (unitilsTestListener == null) {
            unitilsTestListener = createUnitilsTestListener();
        }
        return unitilsTestListener;
    }

    protected UnitilsTestListener createUnitilsTestListener() {
        BootstrapContextFactory bootstrapContextFactory = new BootstrapContextFactory();
        Context context = bootstrapContextFactory.create();
        return context.getInstanceOfType(UnitilsTestListener.class);
    }
}
