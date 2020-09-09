/*
 * Copyright 2008, Unitils.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.inject;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.annotation.UsedForTesting;
import org.unitils.inject.annotation.InjectInto;

import static org.junit.Assert.fail;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoExceptionsTest
    extends UnitilsJUnit4 {
    private static final Logger logger = LoggerFactory.getLogger(InjectModuleInjectIntoExceptionsTest.class);

    private TestInject_TargetIsNull testInject_targetIsNull = new TestInject_TargetIsNull();

    private TestInject_TargetDoesntExist testInject_targetDoesntExist = new TestInject_TargetDoesntExist();

    private TestInject_NoTargetSpecified testInject_noTargetSpecified = new TestInject_NoTargetSpecified();

    private TestInject_InvalidOGNLExpression testInject_invalidOGNLExpression = new TestInject_InvalidOGNLExpression();

    private TestInject_NonExistingPropertyInOGNLExpression testInject_nonExistingPropertyInOGNLExpression = new TestInject_NonExistingPropertyInOGNLExpression();

    private InjectModule injectModule = new InjectModule();

    @Before
    public void setUp()
        throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        injectModule.init(configuration);
    }

    @Test
    public void testInject_targetIsNull() {
        try {
            injectModule.injectObjects(testInject_targetIsNull);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug("", e);
        }
    }

    @Test
    public void testInject_targetDoesntExist() {
        try {
            injectModule.injectObjects(testInject_targetDoesntExist);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug("", e);
        }
    }

    @Test
    public void testInject_noTargetSpecified() {
        try {
            injectModule.injectObjects(testInject_noTargetSpecified);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug("", e);
        }
    }

    @Test
    public void testInject_invalidOGNLExpression() {
        try {
            injectModule.injectObjects(testInject_invalidOGNLExpression);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug("", e);
        }
    }

    @Test
    public void testInject_NonExistingPropertyInOGNLExpression() {
        try {
            injectModule.injectObjects(testInject_nonExistingPropertyInOGNLExpression);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug("", e);
        }
    }

    public class TestInject_TargetIsNull {
        @InjectInto(target = "injectOn", property = "injectOnProperty")
        private ToInject toInject;

        @UsedForTesting
        private InjectOn injectOn;
    }

    public class TestInject_TargetDoesntExist {
        @InjectInto(target = "nonExisting", property = "injectOnProperty")
        private ToInject toInject;
    }

    public class TestInject_NoTargetSpecified {
        @InjectInto(property = "injectOnProperty")
        private ToInject toInject;
    }

    public class TestInject_InvalidOGNLExpression {
        @InjectInto(target = "injectOn", property = "@#{[^")
        private ToInject toInject;

        @UsedForTesting
        private InjectOn injectOn = new InjectOn();
    }

    public class TestInject_NonExistingPropertyInOGNLExpression {
        @InjectInto(target = "injectOn", property = "nonExisting")
        private ToInject toInject;

        @UsedForTesting
        private InjectOn injectOn = new InjectOn();
    }

    /**
     * Object to inject
     */
    public class ToInject {
    }

    /**
     * Object to inject into
     */
    public class InjectOn {
    }
}
