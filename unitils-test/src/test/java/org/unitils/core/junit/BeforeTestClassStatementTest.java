/*
 * Copyright (c) Smals
 */
package org.unitils.core.junit;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.core.TestListener;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

/**
 * Test {@link BeforeTestClassStatementTest}.
 * 
 * @author wiw
 * @since 3.4.1
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class BeforeTestClassStatementTest {
    @Mock
    private TestListener listener;

    @Mock
    private Statement statement;

    @Before
    public void setUp()
        throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.BeforeTestClassStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnTestListener()
        throws Throwable {
        Class<?> clazz = TestClass2.class;

        listener.beforeTestClass(clazz);
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new BeforeTestClassStatement(listener, statement, clazz).evaluate();
    }

    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnStatement()
        throws Throwable {
        Class<?> clazz = TestClass2.class;

        listener.beforeTestClass(clazz);
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new BeforeTestClassStatement(listener, statement, clazz).evaluate();
    }

    @Test
    public void testEvaluateOk()
        throws Throwable {
        Class<?> clazz = TestClass2.class;

        listener.beforeTestClass(clazz);
        statement.evaluate();

        EasyMockUnitils.replay();
        new BeforeTestClassStatement(listener, statement, clazz).evaluate();
    }

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
