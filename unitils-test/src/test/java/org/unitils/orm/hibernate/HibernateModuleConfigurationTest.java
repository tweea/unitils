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
package org.unitils.orm.hibernate;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the loading of the configuration in the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleConfigurationTest
    extends UnitilsJUnit4 {
    /* Tested object */
    private HibernateModule hibernateModule;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp()
        throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        hibernateModule = new HibernateModule();
        hibernateModule.init(configuration);
    }

    /**
     * Tests loading of a configuration location specified on class-level.
     */
    @Test
    public void testGetHibernateConfiguration_classLevel() {
        HibernateTestClassLevel hibernateTestClassLevel = new HibernateTestClassLevel();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTestClassLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/orm/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }

    /**
     * Tests loading of a configuration annotation on class-level but no location. An exception should be raised.
     */
    @Test
    public void testGetHibernateConfiguration_classLevelNoLocation() {
        HibernateTestClassLevelNoLocation hibernateTestClassLevelNoLocation = new HibernateTestClassLevelNoLocation();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> hibernateModule.getConfigurationObject(hibernateTestClassLevelNoLocation));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Tests loading of a configuration location specified on field-level.
     */
    @Test
    public void testGetHibernateConfiguration_fieldLevel() {
        HibernateTestFieldLevel hibernateTestFieldLevel = new HibernateTestFieldLevel();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTestFieldLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/orm/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }

    /**
     * Tests loading of a configuration annotation on field-level but no location. An exception should be raised.
     */
    @Test
    public void testGetHibernateConfiguration_fieldLevelNoLocation() {
        HibernateTestFieldLevelNoLocation hibernateTestFieldLevelNoLocation = new HibernateTestFieldLevelNoLocation();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> hibernateModule.getConfigurationObject(hibernateTestFieldLevelNoLocation));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Tests loading of a configuration location specified on field-level.
     */
    @Test
    public void testGetHibernateConfiguration_setterLevel() {
        HibernateTestSetterLevel hibernateTestSetterLevel = new HibernateTestSetterLevel();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTestSetterLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/orm/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }

    /**
     * Tests loading of a configuration annotation on field-level but no location. An exception should be raised.
     */
    @Test
    public void testGetHibernateConfiguration_setterLevelNoLocation() {
        HibernateTestSetterLevelNoLocation hibernateTestSetterLevelNoLocation = new HibernateTestSetterLevelNoLocation();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> hibernateModule.getConfigurationObject(hibernateTestSetterLevelNoLocation));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Tests for more than 1 annotation with values. An exception should have been raised.
     */
    @Test
    public void testGetApplicationContext_multipleAnnotationsWithValues() {
        HibernateTestMultipleAnnotationsWithValues hibernateTestMultipleAnnotationsWithValues = new HibernateTestMultipleAnnotationsWithValues();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> hibernateModule.getConfigurationObject(hibernateTestMultipleAnnotationsWithValues));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Tests loading of a configuration using a custom create method with a wrong signature.
     */
    @Test
    public void testGetHibernateConfiguration_customCreateWrongSignature() {
        HibernateTestCustomCreateWrongSignature hibernateTestCustomCreateWrongSignature = new HibernateTestCustomCreateWrongSignature();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class,
            () -> hibernateModule.getConfigurationObject(hibernateTestCustomCreateWrongSignature));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Tests calling a custom initialization.
     */
    @Test
    public void testGetHibernateConfiguration_customInitializationMethod() {
        HibernateTestCustomInitialization hibernateTestCustomInitialization = new HibernateTestCustomInitialization();
        hibernateModule.getConfigurationObject(hibernateTestCustomInitialization);

        assertTrue(hibernateTestCustomInitialization.initCalled);
    }

    /**
     * Tests loading of a configuration with a wrong location.
     */
    @Test
    public void testGetHibernateConfiguration_wrongLocation() {
        HibernateTestWrongLocation hibernateTestWrongLocation = new HibernateTestWrongLocation();
        UnitilsException exception = catchThrowableOfType(UnitilsException.class, () -> hibernateModule.getConfigurationObject(hibernateTestWrongLocation));
        assertThat(exception).as("Expected UnitilsException").isNotNull();
    }

    /**
     * Test reusing a configuration for the same class.
     */
    @Test
    public void testGetHibernateConfiguration_twice() {
        Configuration hibernateConfiguration1 = hibernateModule.getConfigurationObject(new HibernateTestClassLevel());
        Configuration hibernateConfiguration2 = hibernateModule.getConfigurationObject(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/orm/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }

    /**
     * Class level configuration.
     */
    @HibernateSessionFactory("org/unitils/orm/hibernate/hibernate.cfg.xml")
    public class HibernateTestClassLevel {
    }

    /**
     * Class level configuration no location specified.
     */
    @HibernateSessionFactory
    public class HibernateTestClassLevelNoLocation {
    }

    /**
     * Field level configuration.
     */
    public class HibernateTestFieldLevel {
        @HibernateSessionFactory("org/unitils/orm/hibernate/hibernate.cfg.xml")
        protected SessionFactory field;
    }

    /**
     * Field level configuration no location specified.
     */
    public class HibernateTestFieldLevelNoLocation {
        @HibernateSessionFactory
        protected SessionFactory field;
    }

    /**
     * Setter level configuration.
     */
    public class HibernateTestSetterLevel {
        /**
         * @param sessionFactory
         *     Used for testing
         */
        @HibernateSessionFactory("org/unitils/orm/hibernate/hibernate.cfg.xml")
        public void setField(SessionFactory sessionFactory) {
        }
    }

    /**
     * Setter level configuration no location specified.
     */
    public class HibernateTestSetterLevelNoLocation {
        /**
         * @param sessionFactory
         *     Used for testing
         */
        @HibernateSessionFactory
        public void setField(SessionFactory sessionFactory) {
        }
    }

    /**
     * Test SpringTest class mixing multiple annotations.
     */
    @HibernateSessionFactory({
        "1"
    })
    private class HibernateTestMultipleAnnotationsWithValues {
        @HibernateSessionFactory({
            "2"
        })
        protected SessionFactory field1;

        @HibernateSessionFactory({
            "3"
        })
        protected SessionFactory field2;
    }

    /**
     * Configuration with custom create with wrong signature.
     */
    public class HibernateTestCustomCreateWrongSignature {
        /**
         * @param a
         *     Used for testing
         */
        @HibernateSessionFactory
        public List<?> createMethod(String a) {
            return null;
        }
    }

    /**
     * Configuration with custom initialization with configuration argument.
     */
    @HibernateSessionFactory("org/unitils/orm/hibernate/hibernate.cfg.xml")
    public class HibernateTestCustomInitialization {
        public boolean initCalled = false;

        /**
         * @param configuration
         *     Used for testing
         */
        @HibernateSessionFactory
        public void initializationMethod(Configuration configuration) {
            initCalled = true;
        }
    }

    /**
     * Class level configuration a wrong location specified.
     */
    @HibernateSessionFactory("xxxxxxx")
    public class HibernateTestWrongLocation {
    }
}
