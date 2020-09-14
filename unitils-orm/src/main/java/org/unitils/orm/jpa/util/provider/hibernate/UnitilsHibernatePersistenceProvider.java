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
package org.unitils.orm.jpa.util.provider.hibernate;

import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

/**
 * Subclass of hibernate's own implementation of <code>javax.persistence.spi.PersistenceProvider</code>.
 * Enables getting hold on the <code>org.hibernate.ejb.Ejb3Configuration</code> object that was used for
 * configuring the <code>EntityManagerFactory</code> after it was created.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsHibernatePersistenceProvider
    extends HibernatePersistenceProvider {
    /**
     * The hibernate configuration object that was used for configuring the <code>EntityManagerFactory</code>
     */
    private Configuration hibernateConfiguration;

    @Override
    public EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration,
        ClassLoader providedClassLoader) {
        EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) super.getEntityManagerFactoryBuilder(persistenceUnitDescriptor, integration,
            providedClassLoader);
        hibernateConfiguration = builder.getHibernateConfiguration();
        return builder;
    }

    /**
     * Should not be used until after creating the <code>EntityManagerFactory</code> using
     * {@link #createContainerEntityManagerFactory}
     * 
     * @return The hibernate configuration object that was used for configuring the <code>EntityManagerFactory</code>.
     */
    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }
}
