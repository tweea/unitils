/*
 * Copyright (c) Smals
 */
package org.unitils.core.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * test {@link AfterTestMethodStatement}.
 * 
 * @author wiw
 * @since 3.4.1
 */
public class AfterTestMethodStatementTest {
    @Before
    public void setUp()
        throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.AfterTestMethodStatement#evaluate()}.
     */
    @Test
    public void testEvaluateTestListenerIsNull()
        throws Throwable {
        Statement statement = new AfterTestMethodStatement(null, new Statement() {
            @Override
            public void evaluate()
                throws Throwable {
            }
        }, new TestClass1(), TestClass1.class.getMethod("test1"));
        NullPointerException exception = catchThrowableOfType(() -> statement.evaluate(), NullPointerException.class);
        assertThat(exception).isNotNull();
    }

    @Test
    public void testEvaluateStatementThrowsException()
        throws Throwable {
        Statement statement = new AfterTestMethodStatement(new TestListener() {
        }, new Statement() {
            @Override
            public void evaluate()
                throws Throwable {
                throw new Exception("This is a test exception");
            }
        }, new TestClass2(), TestClass2.class.getMethod("test1"));
        Exception exception = catchThrowableOfType(() -> statement.evaluate(), Exception.class);
        assertThat(exception).isNotNull().hasMessage("This is a test exception");
    }

    private class TestClass1 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
