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
package org.unitils.dbunit.datasetloadstrategy.impl;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * {@link DataSetLoadStrategy} that inserts a dataset, after removal of all data present in the tables specified in the dataset.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @see DatabaseOperation#CLEAN_INSERT
 */
public class CleanInsertLoadStrategy
    extends BaseDataSetLoadStrategy {
    /**
     * Executes this DataSetLoadStrategy. This means the given dataset is inserted in the database using the given dbUnit
     * database connection object.
     *
     * @param dbUnitDatabaseConnection
     *     DbUnit class providing access to the database
     * @param dataSet
     *     The dbunit dataset
     */
    @Override
    public void doExecute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet)
        throws DatabaseUnitException, SQLException {
        DatabaseOperation.CLEAN_INSERT.execute(dbUnitDatabaseConnection, dataSet);
    }
}
