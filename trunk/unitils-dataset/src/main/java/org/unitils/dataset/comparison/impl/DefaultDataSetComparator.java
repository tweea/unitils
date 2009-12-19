/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.comparison.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.DataSetComparator;
import org.unitils.dataset.core.*;
import org.unitils.dataset.util.ComparisonPreparedStatementWrapper;
import org.unitils.dataset.util.ResultSetWrapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparator implements DataSetComparator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDataSetComparator.class);

    protected DataSource dataSource;


    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSetComparison compare(DataSet expectedDataSet, List<String> variables) {
        try {
            return compareDataSet(expectedDataSet, variables);
        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set.", e);
        }
    }


    protected DataSetComparison compareDataSet(DataSet dataSet, List<String> variables) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            DataSetComparison dataSetComparison = new DataSetComparison();
            for (Schema schema : dataSet.getSchemas()) {
                SchemaComparison schemaComparison = compareSchema(schema, variables, connection);
                dataSetComparison.addSchemaComparison(schemaComparison);
            }
            return dataSetComparison;
        } finally {
            connection.close();
        }
    }

    protected SchemaComparison compareSchema(Schema schema, List<String> variables, Connection connection) throws SQLException {
        String schemaName = schema.getName();
        SchemaComparison schemaComparison = new SchemaComparison(schemaName);
        for (Table table : schema.getTables()) {
            TableComparison tableComparison = compareTable(schemaName, table, variables, connection);
            schemaComparison.addTableComparison(tableComparison);
        }
        return schemaComparison;
    }

    protected TableComparison compareTable(String schemaName, Table table, List<String> variables, Connection connection) {
        String tableName = table.getName();
        TableComparison tableDifference = new TableComparison(tableName);
        findMatches(schemaName, tableName, table, variables, connection, tableDifference);
        findBestComparisons(schemaName, tableName, table, variables, connection, tableDifference);
        return tableDifference;
    }

    protected void findMatches(String schemaName, String tableName, Table table, List<String> variables, Connection connection, TableComparison tableComparison) {
        for (Row row : table.getRows()) {
            try {
                ComparisonPreparedStatementWrapper preparedStatementWrapper = createPreparedStatementWrapper(schemaName, tableName, row, variables, connection);
                try {
                    findMatchesAndTablesThatShouldHaveNoMoreRecords(row, variables, preparedStatementWrapper, tableComparison);
                } finally {
                    preparedStatementWrapper.close();
                }
            } catch (Exception e) {
                throw new UnitilsException("Unable to compare data set row for schema: " + schemaName + ", table: " + tableName + ", row: [" + row + "], variables: " + variables, e);
            }
        }
    }

    protected void findBestComparisons(String schemaName, String tableName, Table table, List<String> variables, Connection connection, TableComparison tableComparison) {
        for (Row row : table.getRows()) {
            if (row.isEmpty() || tableComparison.hasMatch(row)) {
                continue;
            }
            try {
                ComparisonPreparedStatementWrapper preparedStatementWrapper = createPreparedStatementWrapper(schemaName, tableName, row, variables, connection);
                try {
                    findBestComparisons(row, variables, preparedStatementWrapper, tableComparison);
                } finally {
                    preparedStatementWrapper.close();
                }
            } catch (Exception e) {
                throw new UnitilsException("Unable to compare data set row for schema: " + schemaName + ", table: " + tableName + ", row: [" + row + "], variables: " + variables, e);
            }
        }
    }


    protected void findMatchesAndTablesThatShouldHaveNoMoreRecords(Row row, List<String> variables, ComparisonPreparedStatementWrapper preparedStatementWrapper, TableComparison tableComparison) throws Exception {
        ResultSetWrapper resultSet = preparedStatementWrapper.executeQuery();
        while (resultSet.next()) {
            String rowIdentifier = resultSet.getRowIdentifier();
            if (tableComparison.isActualRowWithExactMatch(rowIdentifier)) {
                continue;
            }
            if (row.isEmpty()) {
                tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
                break;
            }
            RowComparison rowComparison = compareRow(row, resultSet);
            if (rowComparison.isMatch()) {
                tableComparison.replaceIfBetterRowComparison(rowIdentifier, rowComparison);
                break;
            }
        }
        resultSet.close();
    }

    protected void findBestComparisons(Row row, List<String> variables, ComparisonPreparedStatementWrapper preparedStatementWrapper, TableComparison tableComparison) throws Exception {
        boolean foundActualRow = false;

        ResultSetWrapper resultSet = preparedStatementWrapper.executeQuery();
        Set<String> primaryKeyColumnNames = preparedStatementWrapper.getPrimaryKeyColumnNames();

        while (resultSet.next()) {
            String rowIdentifier = resultSet.getRowIdentifier();
            if (tableComparison.isActualRowWithExactMatch(rowIdentifier)) {
                continue;
            }
            RowComparison rowComparison = compareRow(row, resultSet);
            tableComparison.replaceIfBetterRowComparison(rowIdentifier, rowComparison);
            foundActualRow = true;
        }
        if (!foundActualRow) {
            tableComparison.addMissingRow(row);
        }
        resultSet.close();
        preparedStatementWrapper.close();
    }


    protected RowComparison compareRow(Row row, ResultSetWrapper resultSet) throws SQLException {
        RowComparison rowComparison = new RowComparison(row);

        List<Column> columns = row.getColumns();
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);
            String expectedValue = resultSet.getExpectedValue(index);
            String actualValue = resultSet.getActualValue(index);

            ColumnComparison columnComparison = new ColumnComparison(column, expectedValue, actualValue);
            rowComparison.addColumnComparison(columnComparison);
        }
        return rowComparison;
    }

    protected ComparisonPreparedStatementWrapper createPreparedStatementWrapper(String schemaName, String tableName, Row row, List<String> variables, Connection connection) throws Exception {
        ComparisonPreparedStatementWrapper preparedStatementWrapper = new ComparisonPreparedStatementWrapper(schemaName, tableName, connection);
        for (Column column : row.getColumns()) {
            preparedStatementWrapper.addColumn(column, variables);
        }
        return preparedStatementWrapper;
    }


}