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
package org.unitils.orm.hibernate.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.Target;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertTrue;

/**
 * Assert class that offers assert methods for testing things that are specific to Hibernate.
 *
 * @author Timmy Maris
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateAssert {
    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct.
     *
     * @param configuration
     *     The hibernate config, not null
     * @param session
     *     The hibernate session, not null
     * @param databaseDialect
     *     The database dialect, not null
     */
    public static void assertMappingWithDatabaseConsistent(Configuration configuration, Session session, Dialect databaseDialect) {
        List<String> script = generateDatabaseUpdateScript(configuration, session, databaseDialect);

        List<String> differences = new ArrayList<>();
        for (String line : script) {
            // ignore constraints
            if (line.indexOf("add constraint") == -1) {
                differences.add(line);
            }
        }
        assertTrue("Found mismatches between Java objects and database tables. Applying following DDL statements to the "
            + "database should resolve the problem: \n" + formatErrorMessage(differences), differences.isEmpty());
    }

    /**
     * Generates a <code>String</code> array with DML statements based on the Hibernate mapping files.
     *
     * @param configuration
     *     The hibernate config, not null
     * @param session
     *     The hibernate session, not null
     * @param databaseDialect
     *     The database dialect, not null
     * @return List<String> array of DDL statements that were needed to keep the database in sync with the mapping file
     */
    private static List<String> generateDatabaseUpdateScript(Configuration configuration, Session session, Dialect databaseDialect) {
        try {
            StandardServiceRegistryBuilder ssrBuilder = configuration.getStandardServiceRegistryBuilder();
            StandardServiceRegistry ssr = ssrBuilder.build();
            MetadataSources metadataSources = new MetadataSources(ssr);
            MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
            MetadataImplementor metadata = (MetadataImplementor) metadataBuilder.build();

            ServiceRegistry serviceRegistry = metadata.getMetadataBuildingOptions().getServiceRegistry();
            JdbcConnectionAccess jdbcConnectionAccess = serviceRegistry.getService(JdbcServices.class).getBootstrapJdbcConnectionAccess();

            ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
            SchemaMigrator schemaMigrator = serviceRegistry.getService(SchemaManagementTool.class).getSchemaMigrator(cfgService.getSettings());

            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            DatabaseInformation databaseInformation;
            try {
                databaseInformation = new DatabaseInformationImpl(serviceRegistry, serviceRegistry.getService(JdbcEnvironment.class), jdbcConnectionAccess,
                    metadata.getDatabase().getDefaultNamespace().getPhysicalName().getCatalog(),
                    metadata.getDatabase().getDefaultNamespace().getPhysicalName().getSchema());
            } catch (SQLException e) {
                throw jdbcServices.getSqlExceptionHelper().convert(e, "Error creating DatabaseInformation for schema migration");
            }

            List<String> script = new ArrayList<>();
            Target target = new Target() {
                @Override
                public boolean acceptsImportScriptActions() {
                    return true;
                }

                @Override
                public void prepare() {
                }

                @Override
                public void accept(String action) {
                    script.add(action);
                }

                @Override
                public void release() {
                }
            };
            try {
                schemaMigrator.doMigration(metadata, databaseInformation, true, Arrays.asList(target));
            } finally {
                databaseInformation.cleanup();
            }
            return script;
        } catch (HibernateException e) {
            throw new UnitilsException("Could not retrieve database metadata", e);
        }
    }

    /**
     * Formats the given list of messages.
     *
     * @param messageParts
     *     The different parts of the message
     * @return A formatted message, containing the different message parts.
     */
    private static String formatErrorMessage(List<String> messageParts) {
        StringBuffer message = new StringBuffer();
        for (String messagePart : messageParts) {
            message.append(messagePart);
            message.append(";\n");
        }
        return message.toString();
    }
}
