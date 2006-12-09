/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.hibernate;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Assert class that offers assert methods for testing things that are specific to Hibernate.
 *
 * @author Timmy Maris
 * @author Filip Neven
 */
public class HibernateAssert {

    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct. This method assumes
     * that the {@link HibernateModule} is enabled and correctly configured.
     */
    public static void assertMappingToDatabase() {

        Unitils unitils = Unitils.getInstance();
        HibernateModule hibernateModule = unitils.getModulesRepository().getModuleOfType(HibernateModule.class);
        Configuration configuration = hibernateModule.getHibernateConfiguration();
        Session session = hibernateModule.getCurrentSession();
        Dialect databaseDialect = getDatabaseDialect(configuration);

        assertMappingToDatabase(configuration, session, databaseDialect);
    }

    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct. This method does the
     * same as {@link #assertMappingToDatabase} without parameters, but can also be used without the using the
     * {@link HibernateModule} or the {@link org.unitils.database.DatabaseModule} (this means it can be used separately without
     * using any other feature of Unitils).
     *
     * @param configuration
     * @param session
     * @param databaseDialect
     */
    public static void assertMappingToDatabase(Configuration configuration, Session session, Dialect databaseDialect) {
        String[] script = generateScript(configuration, session, databaseDialect);

        List<String> differences = new ArrayList<String>();
        for (String line : script) {
            // ignore constraints
            if (line.indexOf("add constraint") == -1) {
                differences.add(line);
            }
        }
        Assert.assertTrue("Found mismatches between Java objects and database tables. " +
                "Applying following DDL statements to the database should resolve the problem: \n" +
                formatMessage(differences), differences.isEmpty());
    }

    /**
     * Generates a <code>String</code> array with DML statements based on the Hibernate mapping files.
     *
     * @param dialect
     * @param configuration
     * @return String[] array of DDL statements that were needed to keep the database in sync with the mapping file
     */
    private static String[] generateScript(Configuration configuration, Session session, Dialect dialect) {
        try {
            DatabaseMetadata dbm = new DatabaseMetadata(session.connection(), dialect);
            return configuration.generateSchemaUpdateScript(dialect, dbm);
        } catch (SQLException e) {
            throw new UnitilsException("Could not retrieve database metadata", e);
        }
    }

    /**
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @param configuration
     * @return Dialect
     */
    private static Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (StringUtils.isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }

    /**
     * Formats the given list of messages.
     *
     * @param messageParts The different parts of the message
     * @return A formatted message, containing the different message parts.
     */
    private static String formatMessage(List<String> messageParts) {
        StringBuffer message = new StringBuffer();
        for (String messagePart : messageParts) {
            message.append(messagePart);
            message.append(";\n");
        }
        return message.toString();
    }

}
