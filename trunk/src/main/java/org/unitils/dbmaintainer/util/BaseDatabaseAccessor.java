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
package org.unitils.dbmaintainer.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.SQLHandler;

/**
 * Base class for implementations that access the test database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseDatabaseAccessor implements DatabaseAccessing {

    /**
     * The unitils configuration
     */
    protected Properties configuration;

    /**
     * Provides connections to the unit test database
     */
    protected SQLHandler sqlHandler;

    
    /**
     * DbSupport for the default schema
     */
    protected DbSupport defaultDbSupport;

    /**
     * DbSupport for all schemas
     */
    protected Map<String, DbSupport> dbNameDbSupportMap;


    /**
     * Initializes the database operation class with the given {@link Properties}, {@link DataSource}.
     *
     * @param configuration The configuration, not null
     * @param sqlHandler    The sql handler, not null
     */
    public void init(Properties configuration, SQLHandler sqlHandler, DbSupport defaultDbSupport, Map<String, DbSupport> dbNameDbSupportMap) {
        this.configuration = configuration;
        this.sqlHandler = sqlHandler;
        this.dbNameDbSupportMap = dbNameDbSupportMap;
        this.defaultDbSupport = defaultDbSupport;

        doInit(configuration);
    }


    /**
     * Allows subclasses to perform some extra configuration using the given configuration.
     *
     * @param configuration The configuration, not null
     */
    protected void doInit(Properties configuration) {
        // do nothing
    }

    
    public Set<DbSupport> getDbSupports() {
    	return new HashSet<DbSupport>(dbNameDbSupportMap.values());
    }
}