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

package org.unitilsnew.database.core;

import org.junit.After;
import org.junit.Test;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.database.annotations.TestDataSource;

import javax.sql.DataSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerIsTransactionActiveTest extends UnitilsJUnit4 {

    /* Tested object */
    private TransactionManager transactionManager = new TransactionManager();

    @TestDataSource
    private DataSource dataSource;


    @After
    public void cleanup() {
        TransactionManager.dataSourceTransactionManagers.clear();
    }


    @Test
    public void active() throws Exception {
        transactionManager.startTransaction();
        transactionManager.registerDataSource(dataSource);

        boolean result = transactionManager.isTransactionActive();
        assertTrue(result);
    }

    @Test
    public void notActive() throws Exception {
        boolean result = transactionManager.isTransactionActive();
        assertFalse(result);
    }

    @Test
    public void notActiveIfOnlyMarkedAsStarted() throws Exception {
        transactionManager.startTransaction();

        boolean result = transactionManager.isTransactionActive();
        assertFalse(result);
    }
}
