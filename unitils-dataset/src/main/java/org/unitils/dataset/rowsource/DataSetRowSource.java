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
package org.unitils.dataset.rowsource;

import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;

/**
 * A source for reading out data set rows.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetRowSource {

    String getDataSetName();

    /**
     * Opens the given data set file.
     * Don't forget to call close afterwards.
     */
    void open();

    /**
     * @return The general properties of the data set, not null
     */
    DataSetSettings getDataSetSettings();

    /**
     * @return the next row from the opened data set, null if the end of the data set is reached.
     */
    DataSetRow getNextDataSetRow();

    /**
     * Closes the data set file.
     */
    void close();
}