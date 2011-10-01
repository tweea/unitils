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

import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.loadstrategy.loader.DataSetLoader;
import org.unitils.dataset.loadstrategy.loader.impl.InsertDataSetLoader;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertDataSetStrategy extends BaseLoadDataSetStrategy {

    @Override
    protected DataSetLoader createDataSetLoader(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor) {
        InsertDataSetLoader insertDataSetLoader = new InsertDataSetLoader();
        insertDataSetLoader.init(dataSetRowProcessor, databaseAccessor);
        return insertDataSetLoader;
    }

}