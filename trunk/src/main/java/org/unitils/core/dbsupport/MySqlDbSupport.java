/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.core.dbsupport;

import org.unitils.core.util.StoredIdentifierCase;
import static org.unitils.core.util.StoredIdentifierCase.LOWER_CASE;
import static org.unitils.core.util.StoredIdentifierCase.UPPER_CASE;

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MySql database.
 * <p/>
 * Note: by default MySql uses '`' (back-quote) for quoting identifiers. '"' (double quotes) is only supported in MySql
 * if ANSI_QUOTES sql mode is enabled. Quoting identifiers does not make them case-sensitive. Case-sensitivity is
 * platform dependent. E.g. on UNIX systems identifiers will typically be case-sensitive, on Windows platforms they
 * will be converted to lower-case.
 * <p/>
 * Trigger names are an exception to this: they are always case-sensitive.
 *
 * @author Frederick Beernaert
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MySqlDbSupport extends DbSupport {


    /**
     * Creates support for MySql databases.
     */
    public MySqlDbSupport() {
        super("mysql");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + getSchemaName() + "' and table_type = 'BASE TABLE'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + getSchemaName() + "' and table_type = 'VIEW'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select trigger_name from information_schema.triggers where trigger_schema = '" + getSchemaName() + "'");
    }


    /**
     * Removes all constraints on the specified table
     *
     * @param tableName The table with the column, not null
     */
    @Override
    public void disableConstraints(String tableName) {
        removeForeignKeyConstraints(tableName);
        removeNotNullConstraints(tableName);
    }


    /**
     * Gets the names of all identity columns of the given table.
     * <p/>
     * todo check, at this moment the PK columns are returned
     *
     * @param tableName The table, not null
     * @return The names of the identity columns of the table with the given name
     */
    @Override
    public Set<String> getIdentityColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and column_key = 'PRI' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    @Override
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " AUTO_INCREMENT = " + identityValue);
    }


    /**
     * Converts the given identifier to uppercase/lowercase
     * <p/>
     * MySql does not treat quoted identifiers as case sensitive. These will also be converted to the correct case.
     * <p/>
     * KNOWN ISSUE: MySql trigger names are case-sensitive (even if not quoted). This will incorrectly be converted to
     * the stored identifier case
     *
     * @param identifier The identifier, not null
     * @return The name converted to correct case if needed, not null
     */
    @Override
    public String toCorrectCaseIdentifier(String identifier) {
        identifier = identifier.trim();
        String identifierQuoteString = getIdentifierQuoteString();
        if (identifier.startsWith(identifierQuoteString) && identifier.endsWith(identifierQuoteString)) {
            identifier = identifier.substring(1, identifier.length() - 1);
        }

        StoredIdentifierCase storedIdentifierCase = getStoredIdentifierCase();
        if (storedIdentifierCase == UPPER_CASE) {
            return identifier.toUpperCase();
        } else if (storedIdentifierCase == LOWER_CASE) {
            return identifier.toLowerCase();
        } else {
            return identifier;
        }
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Identity columns are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    // todo rewrite constraint disabling

    /**
     * Disables all foreign key constraints
     *
     * @param tableName The table, not null
     */
    protected void removeForeignKeyConstraints(String tableName) {
        Set<String> constraintNames = getForeignKeyConstraintNames(tableName);
        for (String constraintName : constraintNames) {
            removeForeignKeyConstraint(tableName, constraintName);
        }
    }


    /**
     * Disables all not-null constraints that are not of primary keys.
     *
     * @param tableName The table, not null
     */
    protected void removeNotNullConstraints(String tableName) {
        // Retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
        Set<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(tableName);

        Set<String> notNullColumnNames = getNotNullColummnNames(tableName);
        for (String notNullColumnName : notNullColumnNames) {
            if (primaryKeyColumnNames.contains(notNullColumnName)) {
                // Do not remove PK constraints
                continue;
            }
            removeNotNullConstraint(tableName, notNullColumnName);
        }
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    protected Set<String> getForeignKeyConstraintNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select constraint_name from information_schema.table_constraints where constraint_type = 'FOREIGN KEY' AND table_name = '" + tableName + "' and constraint_schema = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    protected Set<String> getPrimaryKeyColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and column_key = 'PRI' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
     */
    protected Set<String> getNotNullColummnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where is_nullable = 'NO' and table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    protected void removeForeignKeyConstraint(String tableName, String constraintName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " drop foreign key " + quoted(constraintName));
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    protected void removeNotNullConstraint(String tableName, String columnName) {
        String columnType = getSQLHandler().getItemAsString("select column_type from information_schema.columns where table_schema = '" + getSchemaName() + "' and table_name = '" + tableName + "' and column_name = '" + columnName + "'");
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " change column " + quoted(columnName) + " " + quoted(columnName) + " " + columnType + " NULL ");
    }

}