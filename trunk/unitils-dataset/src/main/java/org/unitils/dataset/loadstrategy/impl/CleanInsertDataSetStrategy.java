/*
 * Copyright Unitils.org
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
package org.unitils.dataset.loadstrategy.impl;

import org.dbmaintain.database.IdentifierProcessor;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.List;
import java.util.Properties;

import static org.unitils.util.ExceptionUtils.getAllMessages;

/**
 * First deletes all data from the tables in the data set and then
 * loads the data using insert statements.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDataSetStrategy extends InsertDataSetStrategy {

    protected TableContentDeleter tableContentDeleter;


    @Override
    public void init(Properties configuration, DataSourceWrapper dataSourceWrapper, IdentifierProcessor identifierProcessor) {
        super.init(configuration, dataSourceWrapper, identifierProcessor);
        // todo move out
        tableContentDeleter = new TableContentDeleter(databaseAccessor, dataSourceWrapper);
    }


    @Override
    public void perform(DataSetRowSource dataSetRowSource, List<String> variables) {
        deleteDataFromTablesInReverseOrder(dataSetRowSource);
        super.perform(dataSetRowSource, variables);
    }


    protected void deleteDataFromTablesInReverseOrder(DataSetRowSource dataSetRowSource) {
        try {
            dataSetRowSource.open();
            tableContentDeleter.deleteDataFromTablesInReverseOrder(dataSetRowSource);

        } catch (Exception e) {
            String message = getAllMessages(e);
            throw new UnitilsException("Unable to delete table data for data set file: " + dataSetRowSource.getDataSetName() + "\n" + message, e);
        } finally {
            dataSetRowSource.close();
        }
    }
}