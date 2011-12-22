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

import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.fail;
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSet;
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSetFile;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertExpectedDataSetTest extends OneDbDataSetTestBase {


    @Test
    public void matchingDataSet() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-simple.xml");
    }

    @Test
    public void differentDataSet() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
            DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-different.xml");

        } catch (AssertionError e) {
            assertMessageContains("No match found for data set row:  PUBLIC.TEST [COL1=xxxx, COL2=9999]", e);
            assertMessageContains("Expected:  xxxx    9999", e);
            assertMessageContains("Actual:    value1  1", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test(expected = UnitilsException.class)
    public void literalValuesNotSupportedInExpectedDataSet() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-literalValues.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-literalValues.xml");
    }

    @Test
    public void caseSensitive() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-caseSensitive.xml");
    }

    @Test(expected = UnitilsException.class)
    public void caseSensitiveWrongCase() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-caseSensitiveWrongCase.xml");
    }

    @Test
    public void equalVariables() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-variables.xml", "test", "1");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-variables.xml", "test", "1");
    }

    @Test
    public void differentVariables() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-variables.xml", "test", "1");
            DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-variables.xml", "xxxx", "9999");

        } catch (AssertionError e) {
            assertMessageContains("test", e);
            assertMessageContains("xxxx", e);
            assertMessageContains("1", e);
            assertMessageContains("9999", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void noDatabaseContentLogging() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
            DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-different.xml");

        } catch (AssertionError e) {
            assertMessageNotContains("Actual Database Content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void emptyDataSet() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-simple.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-emptyDataSet.xml");
    }

    @Test
    public void dateCheck() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-date.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-date.xml");
    }

    @Test
    public void timestampCheck() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-timestamp.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-timestamp.xml");
    }

    /**
     * Verify that if the actual timestamp is null, an AssertionError is raised to indicate there is a dataset difference,
     * not a NullPointerException
     */
    @Test(expected = AssertionError.class)
    public void timestampCheck_NullTimestamp() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-nulltimestamp.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-timestamp.xml");
    }

    @Test
    public void timeCheck() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetTest-time.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetTest-time.xml");
    }
}