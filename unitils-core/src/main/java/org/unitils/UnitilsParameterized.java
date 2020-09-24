package org.unitils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parameterized runner.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
public class UnitilsParameterized
    extends Suite {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitilsParameterized.class);

    private static final String METHOD = "Method ";

    private Class<?> clazz;

    /**
     * TestClassRunnerForParameters.
     * 
     * @author wiw
     */
    protected class TestClassRunnerForParameters
        extends UnitilsBlockJUnit4ClassRunner {
        private final int fParameterSetNumber;

        private final List<Object[]> fParameterList;

        public TestClassRunnerForParameters(Class<?> javaClass, List<Object[]> parametersList, int i)
            throws Exception {
            super(javaClass);
            this.fParameterList = parametersList;
            this.fParameterSetNumber = i;
        }

        @Override
        protected Object createTest()
            throws Exception {
            return getfTestClass().getOnlyConstructor().newInstance(computeParams());
        }

        private TestClass getfTestClass() {
            return new TestClass(clazz);
        }

        protected Object[] computeParams()
            throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(
                    String.format("%s.%s() must return a Collection of arrays.", getTestClass().getName(), getParametersMethod(getfTestClass()).getName()), e);
            }
        }

        @Override
        protected String getName() {
            StringBuffer name = new StringBuffer();
            try {
                Object[] data = fParameterList.get(fParameterSetNumber);
                for (Object object : data) {
                    if (object != null) {
                        name.append(object.toString());
                        name.append(",");
                    } else {
                        name.append("null,");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                LOGGER.error(e.getMessage(), e);
            }
            name = new StringBuffer(name.substring(0, name.length() - 1));

            return String.format("dataset [%s]", name.toString());
        }

        @Override
        protected String testName(FrameworkMethod method) {
            return String.format("%s[%s]", method.getName(), fParameterSetNumber);
        }

        @Override
        protected void collectInitializationErrors(List<Throwable> errors) {
            super.collectInitializationErrors(errors);

            UnitilsMethodValidator validator = new UnitilsMethodValidator(getTestClass());
            validator.validateMethodsForParameterizedRunner(errors);
        }

        @Override
        protected void validateZeroArgConstructor(List<Throwable> errors) {
        }
    }

    private final List<Runner> runners = new ArrayList<>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public UnitilsParameterized(Class<?> klass)
        throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        this.clazz = klass;
        List<Object[]> parametersList = getParametersList(getTestClass());
        for (int i = 0; i < parametersList.size(); i++) {
            runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(), parametersList, i));
        }
    }

    private List<Object[]> getParametersList(TestClass testClass)
        throws Exception, Throwable {
        return (List<Object[]>) getParametersMethod(testClass).invokeExplosively(null);
    }

    protected FrameworkMethod getParametersMethod(TestClass testClass)
        throws Exception {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);

        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }

        throw new Exception("No public static parameters method on class " + testClass.getName());
    }

    /**
     * @see org.junit.runners.Suite#getChildren()
     */
    @Override
    protected List<Runner> getChildren() {
        return Collections.unmodifiableList(runners);
    }

    /**
     * UnitilsMethodValidator.
     * 
     * @author wiw
     */
    protected static class UnitilsMethodValidator {
        private TestClass testclass;

        public UnitilsMethodValidator(TestClass testClass) {
            this.testclass = testClass;
        }

        public void validateMethodsForParameterizedRunner(List<Throwable> errors) {
            validateArgConstructor(errors);
            validateStaticMethods(errors);
            validateInstanceMethods(errors);
        }

        protected void validateTestMethods(List<Throwable> errors, Class<? extends Annotation> annotation, boolean isStatic) {
            List<FrameworkMethod> methods = testclass.getAnnotatedMethods(annotation);
            for (FrameworkMethod method : methods) {
                Method each = method.getMethod();
                if (Modifier.isStatic(each.getModifiers()) != isStatic) {
                    String state = isStatic ? "should" : "should not";
                    errors.add(new Exception(METHOD + each.getName() + "() " + state + " be static"));
                }
                if (!Modifier.isPublic(each.getDeclaringClass().getModifiers())) {
                    errors.add(new Exception("Class " + each.getDeclaringClass().getName() + " should be public"));
                }
                if (!Modifier.isPublic(each.getModifiers())) {
                    errors.add(new Exception(METHOD + each.getName() + " should be public"));
                }
                if (each.getReturnType() != Void.TYPE) {
                    errors.add(new Exception(METHOD + each.getName() + " should be void"));
                }
                if (each.getParameterTypes().length != 0) {
                    errors.add(new Exception(METHOD + each.getName() + " should have no parameters"));
                }
            }
        }

        protected void validateInstanceMethods(List<Throwable> errors) {
            validateTestMethods(errors, After.class, false);
            validateTestMethods(errors, Before.class, false);
            validateTestMethods(errors, Test.class, false);

            List<FrameworkMethod> methods = testclass.getAnnotatedMethods(Test.class);
            if (methods.size() == 0) {
                errors.add(new Exception("No runnable methods"));
            }
        }

        protected void validateStaticMethods(List<Throwable> errors) {
            validateTestMethods(errors, BeforeClass.class, true);
            validateTestMethods(errors, AfterClass.class, true);
        }

        protected void validateArgConstructor(List<Throwable> errors) {
            TestClass clazz = new TestClass(testclass.getJavaClass());
            Constructor<?> onlyConstructor = clazz.getOnlyConstructor();

            if (onlyConstructor.getParameterTypes().length == 0) {
                errors.add(new Exception("Test class shouldn't have a public zero-argument constructor"));
            }
        }
    }
}
