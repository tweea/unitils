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
package org.unitils.dbmaintainer.structure.impl;

import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import org.unitils.util.PropertyUtils;

/**
 * Implementation of {@link SequenceUpdater}. All sequences and identity columns that have a value lower than the value
 * defined by {@link #PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE} are set to this value.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultSequenceUpdater
    extends BaseDatabaseAccessor
    implements SequenceUpdater {

    /* Property key for the lowest acceptacle sequence value */
    public static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    /* The logger instance for this class */
    private static Logger logger = LoggerFactory.getLogger(DefaultSequenceUpdater.class);

    /* The lowest acceptable sequence value */
    protected long lowestAcceptableSequenceValue;

    /**
     * Initializes the lowest acceptable sequence value using the given configuration object
     *
     * @param configuration
     *     The config, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        lowestAcceptableSequenceValue = PropertyUtils.getLong(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, configuration);
    }

    /**
     * Updates all database sequences and identity columns to a sufficiently high value, so that test data be inserted
     * easily.
     */
    @Override
    public void updateSequences() {
        for (DbSupport dbSupport : dbSupports) {
            logger.info("Updating sequences and identity columns in database schema " + dbSupport.getSchemaName());
            incrementSequencesWithLowValue(dbSupport);
            incrementIdentityColumnsWithLowValue(dbSupport);
        }
    }

    /**
     * Sets all the sequences to the lowest acceptable value.
     * This can be defined with the property "sequenceUpdater.sequencevalue.lowestacceptable".
     */
    @Override
    public void restartSequences() {
        for (DbSupport dbSupport : dbSupports) {
            restartWithLowValue(dbSupport);
        }
    }

    /**
     * Increments all sequences whose value is too low.
     *
     * @param dbSupport
     *     The database support, not null
     */
    private void incrementSequencesWithLowValue(DbSupport dbSupport) {
        if (!dbSupport.supportsSequences()) {
            return;
        }
        Set<String> sequenceNames = dbSupport.getSequenceNames();
        for (String sequenceName : sequenceNames) {
            if (dbSupport.getSequenceValue(sequenceName) < lowestAcceptableSequenceValue) {
                logger.debug("Incrementing value for sequence " + sequenceName + " in database schema " + dbSupport.getSchemaName());
                dbSupport.incrementSequenceToValue(sequenceName, lowestAcceptableSequenceValue);
            }
        }
    }

    /**
     * Increments the next value for identity columns whose next value is too low
     *
     * @param dbSupport
     *     The database support, not null
     */
    private void incrementIdentityColumnsWithLowValue(DbSupport dbSupport) {
        if (!dbSupport.supportsIdentityColumns()) {
            return;
        }
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            Set<String> identityColumnNames = dbSupport.getIdentityColumnNames(tableName);
            for (String identityColumnName : identityColumnNames) {
                try {
                    dbSupport.incrementIdentityColumnToValue(tableName, identityColumnName, lowestAcceptableSequenceValue);
                    logger.debug("Incrementing value for identity column " + identityColumnName + " in database schema " + dbSupport.getSchemaName());
                } catch (UnitilsException e) {
                    logger.trace("", e);
                    // primary key is not an identity column
                    // skip column
                }
            }
        }
    }

    /**
     * Sets all the sequences to the lowest acceptable value.
     * This can be defined with the property "sequenceUpdater.sequencevalue.lowestacceptable".
     */
    public void restartWithLowValue(DbSupport dbSupport) {
        if (!dbSupport.supportsSequences()) {
            return;
        }
        Set<String> sequenceNames = dbSupport.getSequenceNames();
        for (String sequenceName : sequenceNames) {
            dbSupport.incrementSequenceToValue(sequenceName, lowestAcceptableSequenceValue);
        }
    }
}
