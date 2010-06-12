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
package org.unitils.dataset.rowsource.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.stripStart;

/**
 * Data set row source that reads out a data set from an xml file.
 * <p/>
 * Following format is expected:
 * <code><pre>
 * &lt;dataset&gt;
 *      &lt;first_table  myColumn1="value1" myColumn2="value2" /&gt;
 *      &lt;second_table myColumnA="A" /&gt;
 *      &lt;first_table  myColumn2="other value2" /&gt;
 *      &lt;empty_table /&gt;
 * &lt;/dataset&gt;
 * </pre></code>
 * <p/>
 * Elements for a table may occur more than once and anywhere in the data set. If multiple elements
 * exist, they may specify different attributes (columns). Missing attributes (columns) will be treated as null values.
 * <p/>
 * Namespaces can be used to specify tables from different database schemas. The namespace URI should contain the name
 * of the database schema:
 * <code><pre>
 * &lt;dataset xmlns="SCHEMA_A" xmlns:b="SCHEMA_B"&gt;
 *      &lt;first_table  myColumn1="value1" myColumn2="value2" /&gt;
 *      &lt;b:second_table myColumnA="A" /&gt;
 *      &lt;first_table  myColumn2="other value2" /&gt;
 *      &lt;empty_table /&gt;
 * &lt;/dataset&gt;
 * </pre></code>
 * <p/>
 * This example defines 2 schemas: SCHEMA_A and SCHEMA_B. The first schema is set as default schema (=default namespace).
 * The 'first_table' table has no namespce and is therefore linked to SCHEMA_A. The 'second_table' table is prefixed
 * with namespace b which is linked to SCHEMA_B. If no default namespace is defined, the schema that is
 * passed as constructor argument is taken as default schema.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineDataSetRowSource implements DataSetRowSource {

    protected List<String> inlineDataSet;
    /* The schema name to use when none is specified */
    protected String defaultSchemaName;
    /* The default settings of the data set */
    protected DataSetSettings defaultDataSetSettings;

    protected int currentIndex;

    public InlineDataSetRowSource(List<String> inlineDataSet, String defaultSchemaName, DataSetSettings defaultDataSetSettings) {
        this.inlineDataSet = inlineDataSet;
        this.defaultSchemaName = defaultSchemaName;
        this.defaultDataSetSettings = defaultDataSetSettings;
    }

    public String getDataSetName() {
        return "<inline data set>";
    }

    /**
     * Opens the data set file.
     * Don't forget to call close afterwards.
     */
    public void open() {
        currentIndex = 0;
    }


    /**
     * @return the next row from the data set, null if the end of the data set is reached.
     */
    public DataSetRow getNextDataSetRow() {
        if (currentIndex >= inlineDataSet.size()) {
            return null;
        }
        String row = inlineDataSet.get(currentIndex);
        currentIndex++;

        try {
            StringBuilder qualifiedTableName = new StringBuilder();
            String trimmedRow = stripStart(row, null);

            boolean notExists = isNotExists(trimmedRow);
            if (notExists) {
                trimmedRow = stripStart(trimmedRow.substring(1), null);
            }

            int index = parseQualifiedTableName(trimmedRow, qualifiedTableName);
            String schemaName = getSchemaName(qualifiedTableName.toString());
            String tableName = getTableName(qualifiedTableName.toString());

            DataSetRow dataSetRow = new DataSetRow(schemaName, tableName, null, notExists, defaultDataSetSettings);
            addDataSetColumns(trimmedRow, index, dataSetRow);
            return dataSetRow;

        } catch (Exception e) {
            throw new UnitilsException("Unable to parse inline data set row: " + row +
                    "\nExpected following format: (schema.)table col1=value, col2=value\n" +
                    "If the value contains a comma, it should be escaped by adding another comma (,,).\n" +
                    "A value can also be placed inside single quotes ('). In that case, commas do not need to be escaped.\n" +
                    "If such a value contains a quote itself, it needs to be escaped in the same way by adding another quote ('').\n" +
                    "Example: column1=comma,,value, column2=quote'value   or   column1='comma,value' column2='quote''value'", e);
        }
    }

    protected int parseQualifiedTableName(String row, StringBuilder qualifiedTableName) {
        int index = row.indexOf(' ');
        if (index == -1) {
            throw new UnitilsException("Table name could not be found. A row should begin with (schema_name.)table_name followed by a space. Data set row: " + row);
        }
        qualifiedTableName.append(row.substring(0, index));
        return index;
    }

    protected String getSchemaName(String qualifiedTableName) {
        int index = qualifiedTableName.indexOf('.');
        if (index == -1) {
            return defaultSchemaName;
        }
        return qualifiedTableName.substring(0, index);
    }

    protected String getTableName(String qualifiedTableName) {
        int index = qualifiedTableName.indexOf('.');
        if (index == -1) {
            return qualifiedTableName;
        }
        return qualifiedTableName.substring(index + 1);
    }

    protected void addDataSetColumns(String row, int fromIndex, DataSetRow dataSetRow) {
        if (fromIndex >= row.length()) {
            return;
        }
        int equalsIndex = row.indexOf('=', fromIndex);
        String columnName = row.substring(fromIndex, equalsIndex).trim();

        StringBuilder value = new StringBuilder();
        int nextCommaIndex = parseNextValue(row, equalsIndex + 1, value);

        DataSetValue dataSetValue = new DataSetValue(columnName, value.toString());
        dataSetRow.addDataSetValue(dataSetValue);

        addDataSetColumns(row, nextCommaIndex + 1, dataSetRow);
    }

    protected int parseNextValue(String row, int index, StringBuilder value) {
        if (isQuotedValue(row, index)) {
            return parseNextQuotedValue(row, index, value);

        }
        return parseValueUntilNextSeparator(row, ',', index, value);
    }

    private int parseNextQuotedValue(String row, int index, StringBuilder value) {
        int firstQuoteIndex = row.indexOf('\'', index);
        index = parseValueUntilNextSeparator(row, '\'', firstQuoteIndex + 1, value);
        if (index == row.length()) {
            throw new UnitilsException("Quoted value was not closed.");
        }
        int nextCommaIndex = row.indexOf(',', index);
        if (nextCommaIndex == -1) {
            if (index < (row.length() - 1)) {
                String textAfterFinalQuotes = row.substring(index + 1);
                if (!isBlank(textAfterFinalQuotes)) {
                    throw new UnitilsException("Found invalid text after final quote: " + textAfterFinalQuotes);
                }
            }
            return row.length();
        }
        return nextCommaIndex;
    }

    protected boolean isNotExists(String row) {
        return row.startsWith("!");
    }

    protected boolean isQuotedValue(String row, int index) {
        int firstQuoteIndex = row.indexOf('\'', index);
        if (firstQuoteIndex == -1) {
            return false;
        }
        String textBeforeQuote = row.substring(index, firstQuoteIndex);
        return isBlank(textBeforeQuote);
    }


    protected int parseValueUntilNextSeparator(String row, char separator, int index, StringBuilder value) {
        while (index < row.length()) {
            char c1 = row.charAt(index);
            if (c1 == separator) {
                if (index == row.length() - 1) {
                    break;
                }
                char c2 = row.charAt(index + 1);
                if (c2 != separator) {
                    // found the end of the value
                    break;
                }
                // escaped
                index++;
            }
            value.append(c1);
            index++;
        }
        return index;
    }


    /**
     * Closes the data set file.
     */
    public void close() {
        // nothing to close
    }


}