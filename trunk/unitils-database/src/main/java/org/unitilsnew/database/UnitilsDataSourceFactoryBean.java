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
package org.unitilsnew.database;

import org.springframework.beans.factory.FactoryBean;
import org.unitilsnew.core.engine.Unitils;
import org.unitilsnew.database.core.DataSourceWrapper;
import org.unitilsnew.database.core.DataSourceWrapperManager;

import javax.sql.DataSource;

/**
 * Spring <code>FactoryBean</code> that provides access to the datasource configured in unitils.
 * <p/>
 * For example, you can define a bean in spring named 'dataSource' that connects to the test database as follows:
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSourceFactoryBean"/&gt;
 * </code></pre>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceFactoryBean implements FactoryBean {

    protected static DataSourceWrapperManager dataSourceWrapperManager = Unitils.getInstanceOfType(DataSourceWrapperManager.class);

    protected String databaseName;


    /**
     * Gets the data source instance.
     *
     * @return The data source, not null
     */
    public Object getObject() throws Exception {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getWrappedDataSource();
    }


    /**
     * @return The database name, null for the default database
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName The database name, null for the default database
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }


    /**
     * Gets the type of the object provided by this <code>FactoryBean</code>, i.e. <code>DataSource</code>
     *
     * @return The type, not null
     */
    public Class<?> getObjectType() {
        return DataSource.class;
    }


    /**
     * @return true, this is a singleton
     */
    public boolean isSingleton() {
        return true;
    }
}
