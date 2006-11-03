/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;

/**
 * todo javadoc
 */
public abstract class UnitilsJUnit3 extends TestCase {


    private static TestListener testListener;

    private static boolean beforeAllCalled;

    private static Class<?> lastTestClass;


    public UnitilsJUnit3() {
        this(null);
    }

    public UnitilsJUnit3(String name) {
        super(name);

        if (testListener == null) {
            testListener = getUnitils().createTestListener();
            createShutdownHook();
        }
    }


    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                super.run();
                if (lastTestClass != null) {
                    testListener.afterTestClass(lastTestClass);
                }
                testListener.afterAll();
            }
        });
    }


    public void runBare() throws Throwable {

        if (!beforeAllCalled) {
            testListener.beforeAll();
            beforeAllCalled = true;
        }

        Class testClass = getClass();
        if (lastTestClass != testClass) {
            if (lastTestClass != null) {
                testListener.afterTestClass(lastTestClass);
            }
            testListener.beforeTestClass(testClass);
            lastTestClass = testClass;
        }

        testListener.beforeTestSetUp(this);
        try {
            super.runBare();

        } finally {
            testListener.afterTestTearDown(this);
        }
    }


    protected void runTest() throws Throwable {

        testListener.beforeTestMethod(this, getCurrentTestMethod());
        super.runTest();
        testListener.afterTestMethod(this, getCurrentTestMethod());
    }


    protected Unitils getUnitils() {
        Unitils unitils = Unitils.getInstance();
        if (unitils == null) {
            Unitils.initSingletonInstance();
            unitils = Unitils.getInstance();
        }
        return unitils;
    }


    private Method getCurrentTestMethod() {

        String testName = getName();
        if (StringUtils.isEmpty(testName)) {

            throw new UnitilsException("Unable to find current test method. No test name provided (null) for test. Test class: " + getClass());
        }

        try {
            return getClass().getMethod(getName());

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Unable to find current test method. Test name: " + getName() + " , test class: " + getClass(), e);
        }
    }


}
