package org.unitils.core.junit;

import java.lang.reflect.Method;

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
 * test {@link BeforeTestMethodStatement}.
 * 
 * @author Willemijn Wouters
 * @since 3.4.1
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class BeforeTestMethodStatementTest {
    @Mock
    private TestListener listener;

    @Mock
    private Statement statement;

    @Before
    public void setUp()
        throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.BeforeTestMethodStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnListener()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        listener.beforeTestMethod(testObject, testMethod);
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new BeforeTestMethodStatement(listener, statement, testObject, testMethod).evaluate();
    }

    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnStatement()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        listener.beforeTestMethod(testObject, testMethod);
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new BeforeTestMethodStatement(listener, statement, testObject, testMethod).evaluate();
    }

    @Test
    public void testEvaluateOk()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        listener.beforeTestMethod(testObject, testMethod);
        statement.evaluate();

        EasyMockUnitils.replay();
        new BeforeTestMethodStatement(listener, statement, testObject, testMethod).evaluate();
    }

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
