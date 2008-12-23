/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.report.impl;

import static org.apache.commons.lang.StringUtils.rightPad;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.mock.proxy.ProxyInvocation;

import java.util.List;
import java.util.Map;

/**
 * A base class for reports about proxy invocations.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public abstract class ProxyInvocationsReport {

    /**
     * The maxium depth for the object formatter that will format the values
     */
    public static int OBJECT_FORMATTER_MAX_RECURSION_DEPT = 10;

    /**
     * The maximum length of an inline value
     */
    public static int MAX_INLINE_PARAMETER_LENGTH = 20;

    /**
     * Formatter for arguments and return values
     */
    protected ObjectFormatter objectFormatter = new ObjectFormatter(OBJECT_FORMATTER_MAX_RECURSION_DEPT);


    /**
     * Creates a string representation of the details of the given invocation. This will give information about
     * where the invocation occurred.
     *
     * @param proxyInvocation The invocation to format, not null
     * @return The string representation, not null
     */
    protected String formatInvokedAt(ProxyInvocation proxyInvocation) {
        return "  .....  at " + proxyInvocation.getInvokedAt();
    }


    /**
     * Formats the given value. If the value is small enough (lenght <=20), the value itself is returned, else
     * a name is returned generated by the {@link #createLargeValueName} method.
     * <p/>
     * E.g. string1, myClass1
     *
     * @param object                 The value to format, not null
     * @param type                   The type of the large value, not null
     * @param currentLargeObjects    The current the large values, not null
     * @param allLargeObjects        All large values per value, not null
     * @param largeObjectNameIndexes The current indexes to use for the large value names (per value type), not null
     * @return The value or the replaced name, not null
     */
    protected String formatValue(Object object, Class<?> type, List<FormattedObject> currentLargeObjects, Map<Object, FormattedObject> allLargeObjects, Map<Class<?>, Integer> largeObjectNameIndexes) {
        if (allLargeObjects.containsKey(object)) {
            FormattedObject formattedObject = allLargeObjects.get(object);
            currentLargeObjects.add(formattedObject);
            return formattedObject.getName();
        }

        String objectRepresentation = objectFormatter.format(object);
        if (objectRepresentation.length() <= MAX_INLINE_PARAMETER_LENGTH) {
            // The object representation is small enough to be shown inline
            return objectRepresentation;
        }
        // The object representation is to large to be shown inline. Generate a name for it, which can be shown as a replacement.
        String largeObjectName = createLargeValueName(type, largeObjectNameIndexes);
        FormattedObject formattedObject = new FormattedObject(largeObjectName, objectRepresentation);
        allLargeObjects.put(object, formattedObject);
        currentLargeObjects.add(formattedObject);
        return largeObjectName;
    }


    /**
     * Creates a name to replace a large value.
     * The name is derived from the given type and index. E.g. string1, myClass1
     *
     * @param type                   The type of the large value, not null
     * @param largeObjectNameIndexes The current indexes per type, not null
     * @return The name, not null
     */
    protected String createLargeValueName(Class<?> type, Map<Class<?>, Integer> largeObjectNameIndexes) {
        Integer index = largeObjectNameIndexes.get(type);
        if (index == null) {
            index = 0;
        }
        largeObjectNameIndexes.put(type, ++index);

        String result = uncapitalize(type.getSimpleName());
        return result + index;
    }


    /**
     * Formats the invocation number, and adds spaces to make sure everything is formatted
     * nicely on the same line width.
     *
     * @param invocationIndex       The index of the invcation
     * @param totalInvocationNumber The total number of invocations.
     * @return The formatted invocation number
     */
    protected String formatInvocationIndex(int invocationIndex, int totalInvocationNumber) {
        int padSize = String.valueOf(totalInvocationNumber).length() + 2;
        return rightPad(invocationIndex + ".", padSize);
    }


    /**
     * Class for representing a value that was too large to be displayed inline.
     */
    protected static class FormattedObject {

        /* The name used as inline replacement */
        private String name;

        /* The actual string representation of the value */
        private String representation;

        /**
         * Creates a large value
         *
         * @param name           The name used as inline replacement, not null
         * @param representation The actual string representation of the value, not null
         */
        public FormattedObject(String name, String representation) {
            this.name = name;
            this.representation = representation;
        }


        /**
         * @return The name used as inline replacement, not null
         */
        public String getName() {
            return name;
        }


        /**
         * @return The actual string representation of the value, not null
         */
        public String getRepresentation() {
            return representation;
        }
    }
}
