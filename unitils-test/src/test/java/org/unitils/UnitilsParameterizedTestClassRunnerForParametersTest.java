package org.unitils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.core.annotation.UsedForTesting;
import org.unitils.reflectionassert.ReflectionAssert;

/**
 * Parameterized runner.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
public class UnitilsParameterizedTestClassRunnerForParametersTest {
    private UnitilsParameterized unitilsParameterized;

    private TestClassRunnerForParameters sut;

    @Before
    public void init()
        throws Throwable {
        unitilsParameterized = new UnitilsParameterized(Testclass1.class);
    }

    @Test
    public void testOneParameter()
        throws Exception {
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            null
        });
        sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);

        String actual = sut.getName();
        String expected = "dataset [null]";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMultipleParametersArrays()
        throws Exception {
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            1, 2
        });
        parameters.add(new Object[] {
            3, 4
        });
        sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 1);

        String actual = sut.getName();
        String expected = "dataset [3,4]";
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBounds()
        throws Exception {
        List<Object[]> parameters = new ArrayList<>();
        sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 1);

        sut.getName();
    }

    @Test
    public void testTestName()
        throws Exception {
        FrameworkMethod method = new FrameworkMethod(Testclass1.class.getMethod("testMethod1"));
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            1, 2
        });
        sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);

        String actual = sut.testName(method);
        String expected = "testMethod1[0]";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testComputeParams()
        throws Exception {
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            1, 2
        });
        sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);

        Object[] actual = sut.computeParams();
        Object[] expected = new Object[] {
            1, 2
        };
        ReflectionAssert.assertLenientEquals(expected, actual);
    }

    public static class Testclass1 {
        @UsedForTesting
        private int number;

        @Parameters
        public static Collection<Object[]> data() {
            return Collections.emptyList();
        }

        public Testclass1(int number) {
            this.number = number;
        }

        @Test
        public void testMethod1() {
            // do nothing
        }
    }
}
