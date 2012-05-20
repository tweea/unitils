/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database.dbmaintain;

import org.dbmaintain.database.DatabaseConnection;
import org.dbmaintain.database.DatabaseConnectionManager;
import org.dbmaintain.database.DatabaseInfo;
import org.dbmaintain.database.SQLHandler;
import org.unitilsnew.database.config.DatabaseConfiguration;
import org.unitilsnew.database.config.DatabaseConfigurations;
import org.unitilsnew.database.core.DataSourceWrapper;
import org.unitilsnew.database.core.DataSourceWrapperManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainDatabaseConnectionManager implements DatabaseConnectionManager {

    protected DatabaseConfigurations databaseConfigurations;
    protected DataSourceWrapperManager dataSourceWrapperManager;
    protected DbMaintainSQLHandler dbMaintainSQLHandler;

    protected Map<String, DatabaseConnection> databaseConnections = new HashMap<String, DatabaseConnection>();


    public DbMaintainDatabaseConnectionManager(DatabaseConfigurations databaseConfigurations, DataSourceWrapperManager dataSourceWrapperManager, DbMaintainSQLHandler dbMaintainSQLHandler) {
        this.databaseConfigurations = databaseConfigurations;
        this.dataSourceWrapperManager = dataSourceWrapperManager;
        this.dbMaintainSQLHandler = dbMaintainSQLHandler;
    }


    public SQLHandler getSqlHandler() {
        return dbMaintainSQLHandler;
    }

    public DatabaseConnection getDatabaseConnection(String databaseName) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        DatabaseConnection databaseConnection = databaseConnections.get(databaseName);
        if (databaseConnection == null) {
            databaseConnection = createDatabaseConnection(databaseName);
            databaseConnections.put(databaseName, databaseConnection);
        }
        return databaseConnection;
    }

    public List<DatabaseConnection> getDatabaseConnections() {
        List<DatabaseConnection> databaseConnections = new ArrayList<DatabaseConnection>();
        List<String> databaseNames = databaseConfigurations.getDatabaseNames();
        for (String databaseName : databaseNames) {
            DatabaseConnection databaseConnection = getDatabaseConnection(databaseName);
            databaseConnections.add(databaseConnection);
        }
        if (databaseConnections.isEmpty()) {
            DatabaseConnection defaultDatabaseConnection = getDatabaseConnection(null);
            databaseConnections.add(defaultDatabaseConnection);
        }
        return databaseConnections;
    }


    protected DatabaseConnection createDatabaseConnection(String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);

        DataSource dataSource = dataSourceWrapper.getWrappedDataSource();
        DatabaseConfiguration databaseConfiguration = dataSourceWrapper.getDatabaseConfiguration();
        DatabaseInfo databaseInfo = createDatabaseInfo(databaseConfiguration);
        return new DatabaseConnection(databaseInfo, dbMaintainSQLHandler, dataSource);
    }

    protected DatabaseInfo createDatabaseInfo(DatabaseConfiguration databaseConfiguration) {
        String databaseName = databaseConfiguration.getDatabaseName();
        String dialect = databaseConfiguration.getDialect();
        String driverClassName = databaseConfiguration.getDriverClassName();
        String url = databaseConfiguration.getUrl();
        String userName = databaseConfiguration.getUserName();
        String password = databaseConfiguration.getPassword();
        String defaultSchemaName = databaseConfiguration.getDefaultSchemaName();
        List<String> schemaNames = databaseConfiguration.getSchemaNames();
        boolean disabled = databaseConfiguration.isDisabled();
        boolean defaultDatabase = databaseConfiguration.isDefaultDatabase();

        return new DatabaseInfo(databaseName, dialect, driverClassName, url, userName, password, schemaNames, disabled, defaultDatabase);
    }

}