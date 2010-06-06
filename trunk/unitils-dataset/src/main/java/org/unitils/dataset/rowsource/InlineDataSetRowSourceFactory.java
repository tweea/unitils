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

import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface InlineDataSetRowSourceFactory {

    /**
     * @param configuration     The unitils configuration, not null
     * @param defaultSchemaName The schema name to use when none is specified, not null
     */
    void init(Properties configuration, String defaultSchemaName);

    DataSetRowSource createDataSetRowSource(List<String> dataSetRows);

}