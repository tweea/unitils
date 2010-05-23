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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.*;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.util.AssertionUtils.assertExceptionMessageContains;

/**
 * Tests for creating using insert statements for loading data rows
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateDataSetLoaderPrimaryKeyTest extends UnitilsJUnit4 {

    /* Tested object */
    private UpdateDataSetLoader updateDataSetLoader = new UpdateDataSetLoader();

    private Mock<DataSetRowSource> dataSetRowSource;
    private Mock<DataSetRowProcessor> dataSetRowProcessor;
    private Mock<DatabaseAccessor> databaseAccessor;

    private List<String> emptyVariables = new ArrayList<String>();

    private DataSetRow dataSetRow;
    private DatabaseRow databaseRowPrimaryKey;
    private DatabaseRow databaseRowNoPrimaryKey;


    @Before
    public void initialize() throws Exception {
        updateDataSetLoader.init(dataSetRowProcessor.getMock(), databaseAccessor.getMock());

        dataSetRow = createDataSetRow();
        databaseRowPrimaryKey = createDatabaseRow(true);
        databaseRowNoPrimaryKey = createDatabaseRow(false);

        databaseAccessor.returns(1).executeUpdate(null, null);
        dataSetRowProcessor.returns(databaseRowPrimaryKey).process(null, null, null);
    }


    @Test
    public void noMissingPkColumnValues() throws Exception {
        updateDataSetLoader.loadDataSetRow(dataSetRow, emptyVariables);
        databaseAccessor.assertInvoked().executeUpdate(null, null);
    }

    @Test
    public void missingPkColumnValues() throws Exception {
        setUnusedPrimaryKeys(asList("unusedPk1", "unusedPk2"));

        try {
            updateDataSetLoader.loadDataSetRow(dataSetRow, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionContainsPkColumnNames(e, "unusedPk1", "unusedPk2");
        }
    }

    @Test
    public void noPkFound() throws Exception {
        dataSetRowProcessor.onceReturns(databaseRowNoPrimaryKey).process(null, null, null);
        try {
            updateDataSetLoader.loadDataSetRow(dataSetRow, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionMessageContains(e.getCause(), "no values for primary keys were specified");
        }
    }


    private void setUnusedPrimaryKeys(final List<String> primaryKeyNames) throws Exception {
        dataSetRowProcessor.oncePerforms(new MockBehavior() {
            @SuppressWarnings({"unchecked"})
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                Set<String> unusedPrimaryKeyColumnNames = (Set<String>) proxyInvocation.getArguments().get(2);
                unusedPrimaryKeyColumnNames.addAll(primaryKeyNames);
                return databaseRowPrimaryKey;
            }
        }).process(null, null, null);
    }


    private DataSetRow createDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));
        return dataSetRow;
    }

    private DatabaseRow createDatabaseRow(boolean primaryKey) {
        DatabaseRow databaseRow = new DatabaseRow("schema.table");
        databaseRow.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column", "value", 0, null, false, primaryKey));
        return databaseRow;
    }


    private void assertExceptionContainsPkColumnNames(UnitilsException e, String... pkColumnNames) {
        String message = e.getCause().getMessage();
        for (String pkColumnName : pkColumnNames) {
            assertTrue("Exception did not contain pk column name: " + pkColumnName + ". Message: " + message, message.contains(pkColumnName));
        }
    }
}