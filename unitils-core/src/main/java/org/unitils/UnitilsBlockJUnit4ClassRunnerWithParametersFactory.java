package org.unitils;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * Parameterized runner factory.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
public class UnitilsBlockJUnit4ClassRunnerWithParametersFactory
    implements ParametersRunnerFactory {
    @Override
    public Runner createRunnerForTestWithParameters(TestWithParameters test)
        throws InitializationError {
        return new UnitilsBlockJUnit4ClassRunnerWithParameters(test);
    }
}
