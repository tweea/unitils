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

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.database.DataSourceWrapper;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.orm.common.OrmModule;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.common.util.OrmConfig;
import org.unitils.orm.common.util.OrmPersistenceUnitLoader;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.orm.hibernate.util.HibernateAnnotationConfigLoader;
import org.unitils.orm.hibernate.util.HibernateAssert;
import org.unitils.orm.hibernate.util.HibernateSessionFactoryLoader;
import org.unitils.util.AnnotationUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Module providing support for unit tests for code that uses Hibernate. It offers an easy way of loading hibernate
 * SessionFactories and having them injected them into the test. It also offers a test to check whether the hibernate
 * mapping is consistent with the structure of the database.
 * <p/>
 * A Hibernate <code>SessionFactory</code> is created when requested and injected into all fields or methods of the test
 * annotated with {@link HibernateSessionFactory}.
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link HibernateUnitils#assertMappingWithDatabaseConsistent()},
 * This is a very useful test that verifies whether the mapping of all your Hibernate mapped objects still corresponds
 * with the actual structure of the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule
    extends OrmModule<SessionFactory, Session, Configuration, HibernateSessionFactory, OrmConfig, HibernateAnnotationConfigLoader> {

    /* The logger instance for this class */
    private static Logger logger = LoggerFactory.getLogger(HibernateModule.class);

    /**
     * @param configuration
     *     The Unitils configuration, not null
     */
    @Override
    public void init(Properties configuration) {
        super.init(configuration);
    }

    @Override
    public void afterInit() {
        super.afterInit();
    }

    public void registerTransactionManagementConfiguration() {
        // Make sure that a spring HibernateTransactionManager is used for transaction management in the database module, if the
        // current test object defines a hibernate SessionFactory
        for (final DataSourceWrapper wrapper : wrappers) {
            getDatabaseModule().registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {
                @Override
                public boolean isApplicableFor(Object testObject) {
                    return isPersistenceUnitConfiguredFor(testObject);
                }

                @Override
                public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                    SessionFactory sessionFactory = getPersistenceUnit(testObject);
                    HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
                    hibernateTransactionManager.setDataSource(getDataSource());
                    return hibernateTransactionManager;
                }

                @Override
                public boolean isTransactionalResourceAvailable(Object testObject) {
                    return wrapper.isDataSourceLoaded();
                }

                @Override
                public Integer getPreference() {
                    return 10;
                }
            });
        }
    }

    @Override
    protected HibernateAnnotationConfigLoader createOrmConfigLoader() {
        return new HibernateAnnotationConfigLoader();
    }

    @Override
    protected Class<HibernateSessionFactory> getPersistenceUnitConfigAnnotationClass() {
        return HibernateSessionFactory.class;
    }

    @Override
    protected Class<SessionFactory> getPersistenceUnitClass() {
        return SessionFactory.class;
    }

    @Override
    protected OrmPersistenceUnitLoader<SessionFactory, Configuration, OrmConfig> createOrmPersistenceUnitLoader() {
        return new HibernateSessionFactoryLoader(databaseName);
    }

    @Override
    protected String getOrmSpringSupportImplClassName() {
        return "org.unitils.orm.hibernate.util.HibernateSpringSupport";
    }

    @Override
    protected Session doGetPersistenceContext(Object testObject) {
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) getPersistenceUnit(testObject);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            SpringSessionContext sessionContext = new SpringSessionContext(sessionFactory);
            return sessionContext.currentSession();
        } else {
            return sessionFactory.openSession();
        }
    }

    @Override
    protected Session doGetActivePersistenceContext(Object testObject) {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getPersistenceUnit(testObject));
        if (sessionHolder != null && sessionHolder.getSession() != null && sessionHolder.getSession().isOpen()) {
            return sessionHolder.getSession();
        }
        return null;
    }

    @Override
    protected void flushOrmPersistenceContext(Session activeSession) {
        logger.info("Flushing session " + activeSession);
        activeSession.flush();
    }

    /**
     * Checks if the mapping of the Hibernate managed objects with the database is correct.
     *
     * @param testObject
     *     The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {
        ConfiguredOrmPersistenceUnit<SessionFactory, Configuration> configuredPersistenceUnit = getConfiguredPersistenceUnit(testObject);
        Configuration configuration = configuredPersistenceUnit.getOrmConfigurationObject();
        Session session = getPersistenceContext(testObject);
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration, session, databaseDialect);
    }

    /**
     * Gets the database dialect from the given Hibernate <code>Configuration</code>.
     *
     * @param configuration
     *     The hibernate config, not null
     * @return the database Dialect, not null
     */
    protected Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }

    protected DataSource getDataSource() {
        return getDatabaseModule().getWrapper(databaseName).getDataSourceAndActivateTransactionIfNeeded();
    }

    /**
     * @return The TestListener associated with this module
     */
    @Override
    public TestListener getTestListener() {
        return new HibernateTestListener();
    }

    /**
     * The {@link TestListener} for this module
     */
    protected class HibernateTestListener
        extends OrmTestListener {
        /**
         * @see org.unitils.core.TestListener#beforeTestMethod(java.lang.Object, java.lang.reflect.Method)
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            // databaseName = getDatabaseName(testObject, testMethod);
            registerTransactionManagementConfiguration();
            super.beforeTestMethod(testObject, testMethod);
        }
    }

    /**
     * @see org.unitils.orm.common.OrmModule#getDatabaseName(java.lang.Object, java.lang.reflect.Method)
     */
    @Override
    protected void getDatabaseName(Object testObject, Method testMethod) {
        // List<String> dataSources = new ArrayList<String>();
        HibernateSessionFactory dataSource = AnnotationUtils.getMethodOrClassLevelAnnotation(HibernateSessionFactory.class, testMethod, testObject.getClass());
        if (dataSource != null) {
            wrappers.add(getDatabaseModule().getWrapper(dataSource.databaseName()));
            // dataSources.add(dataSource.databaseName());
        }

        Set<HibernateSessionFactory> lstDataSources = AnnotationUtils.getFieldLevelAnnotations(testObject.getClass(), HibernateSessionFactory.class);
        if (!lstDataSources.isEmpty()) {
            for (HibernateSessionFactory testDataSource : lstDataSources) {
                // ataSources.add(testDataSource.databaseName());
                wrappers.add(getDatabaseModule().getWrapper(testDataSource.databaseName()));
            }
        }
        // return dataSources;
    }
}
