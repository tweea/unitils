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
 * test {@link AfterCreateTestObjectStatement}.
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class AfterCreateTestObjectStatementTest {
    @Mock
    private TestListener listener;

    @Mock
    private Statement statement;

    @Before
    public void setUp()
        throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.AfterCreateTestObjectStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnListener()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        listener.afterCreateTestObject(testObject);
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new AfterCreateTestObjectStatement(listener, statement, testObject).evaluate();
    }

    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnStatement()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        listener.afterCreateTestObject(testObject);
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());

        EasyMockUnitils.replay();
        new AfterCreateTestObjectStatement(listener, statement, testObject).evaluate();
    }

    @Test
    public void testEvaluateOk()
        throws Throwable {
        TestClass2 testObject = new TestClass2();
        listener.afterCreateTestObject(testObject);
        statement.evaluate();

        EasyMockUnitils.replay();
        new AfterCreateTestObjectStatement(listener, statement, testObject).evaluate();
    }

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
