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
package org.unitils.dataset;

import org.unitils.core.Unitils;
import org.unitils.dataset.loadstrategy.InlineLoadDataSetStrategyHandler;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;

import java.util.ArrayList;
import java.util.List;

import static org.unitils.util.CollectionUtils.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetLoader {

    public static void insertDefaultDataSetFile(Object testInstance, String... variables) {
        insertDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void insertDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        insertDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        insertDataSetFiles(testInstance, dataSetFileNames, false, variables);
    }

    public static void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        getLoadDataSetStrategyHandler().insertDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public static void insertDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler().insertDataSet(dataSetRows);
    }


    public static void cleanInsertDefaultDataSetFile(Object testInstance, String... variables) {
        cleanInsertDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void cleanInsertDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        cleanInsertDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        cleanInsertDataSetFiles(testInstance, dataSetFileNames, false, variables);
    }

    public static void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        getLoadDataSetStrategyHandler().cleanInsertDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public static void cleanInsertDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler().cleanInsertDataSet(dataSetRows);
    }


    public static void refreshDefaultDataSetFile(Object testInstance, String... variables) {
        refreshDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void refreshDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        refreshDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        refreshDataSetFiles(testInstance, dataSetFileNames, false, variables);
    }

    public static void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        getLoadDataSetStrategyHandler().refreshDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public static void refreshDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler().refreshDataSet(dataSetRows);
    }


    private static LoadDataSetStrategyHandler getLoadDataSetStrategyHandler() {
        DataSetModuleFactory dataSetModuleFactory = getDataSetModule().getDataSetModuleFactory();
        return dataSetModuleFactory.getLoadDataSetStrategyHandler();
    }

    private static InlineLoadDataSetStrategyHandler getInlineLoadDataSetStrategyHandler() {
        DataSetModuleFactory dataSetModuleFactory = getDataSetModule().getDataSetModuleFactory();
        return dataSetModuleFactory.getInlineLoadDataSetStrategyHandler();
    }

    /**
     * Gets the instance DataSetModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     */
    private static DataSetModule getDataSetModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
    }
}