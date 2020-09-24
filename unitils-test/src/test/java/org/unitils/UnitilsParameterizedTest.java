package org.unitils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.UnitilsParameterized.UnitilsMethodValidator;
import org.unitils.core.annotation.UsedForTesting;
import org.unitils.parameterized.JustATestClass;
import org.unitils.parameterized.UnitilsParametersNullParametersStveParametersTest;
import org.unitils.util.ReflectionUtils;

/**
 * Parameterized runner.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class UnitilsParameterizedTest {
    @Test
    public void testValidateTestMethods()
        throws Throwable {
        UnitilsMethodValidator methodValidator = new UnitilsMethodValidator(new TestClass(JustATestClass.class));
        List<Throwable> errors = new ArrayList<>();
        methodValidator.validateTestMethods(errors, Test.class, false);
        Assert.assertEquals(getErrors().get(0).getMessage(), errors.get(0).getMessage());
        Assert.assertEquals(getErrors().get(1).getMessage(), errors.get(1).getMessage());
        Assert.assertEquals(getErrors().get(2).getMessage(), errors.get(2).getMessage());
        Assert.assertEquals(getErrors().get(3).getMessage(), errors.get(3).getMessage());
    }

    @Test
    public void testGetParametersMethod() {
        UnitilsMethodValidator methodValidator = new UnitilsMethodValidator(new TestClass(Testclass2.class));
        List<Throwable> errors = new ArrayList<>();
        methodValidator.validateInstanceMethods(errors);
        Assert.assertTrue(errors != null && !errors.isEmpty());
        Assert.assertEquals("No runnable methods", errors.get(0).getMessage());
    }

    @Test(expected = Exception.class)
    public void testComputeParams()
        throws Throwable {
        List<Object[]> data = new ArrayList<>();
        List<int[]> data2 = new ArrayList<>();
        data2.add(new int[] {
            1
        });
        data2.add(new int[] {
            1, 2
        });
        data2.add(new int[] {
            1, 2, 3
        });
        TestClassRunnerForParameters runner = new UnitilsParameterized(
            UnitilsParametersNullParametersStveParametersTest.class).new TestClassRunnerForParameters(UnitilsParametersNullParametersStveParametersTest.class,
                data, 1);
        ReflectionUtils.setFieldValue(runner, "fParameterList", data2);
        runner.computeParams();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetName()
        throws Throwable {
        List<Object[]> data = new ArrayList<>();
        List<int[]> data2 = new ArrayList<>();
        data2.add(new int[] {
            1
        });
        data2.add(new int[] {
            1, 2
        });
        data2.add(new int[] {
            1, 2, 3
        });
        TestClassRunnerForParameters runner = new UnitilsParameterized(
            UnitilsParametersNullParametersStveParametersTest.class).new TestClassRunnerForParameters(UnitilsParametersNullParametersStveParametersTest.class,
                data, 6);
        ReflectionUtils.setFieldValue(runner, "fParameterList", data2);
        runner.getName();
    }

    @Test
    public void testValidateArgConstructorNoParameters()
        throws Exception {
        UnitilsMethodValidator validator = new UnitilsMethodValidator(new TestClass(JustATestClass.class));
        List<Throwable> errors = new ArrayList<>();
        validator.validateArgConstructor(errors);
        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void testValidateArgConstructorWithParameters()
        throws Exception {
        UnitilsMethodValidator validator = new UnitilsMethodValidator(new TestClass(Testclass3.class));
        List<Throwable> errors = new ArrayList<>();
        validator.validateArgConstructor(errors);
        Assert.assertTrue(errors.isEmpty());
    }

    private List<Throwable> getErrors() {
        List<Throwable> lst = new ArrayList<>();
        lst.add(new Exception("Method test1() should not be static"));
        lst.add(new Exception("Method test2 should be public"));
        lst.add(new Exception("Method test3 should be void"));
        lst.add(new Exception("Method test4 should have no parameters"));
        return lst;
    }

    private class Testclass2 {
        // just an empty testclass
    }

    private class Testclass3 {
        @UsedForTesting
        public Testclass3() {
            // do nothing
        }
    }
}
