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
package org.unitils.reflectionassert;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.annotation.UsedForTesting;
import org.unitils.reflectionassert.difference.Difference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;
import static org.unitils.reflectionassert.util.InnerDifferenceFinder.getInnerDifference;

/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests for ignore defaults and lenient dates.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorLenientTest {
    /* Test object with no java defaults */
    private Element elementNoDefaultsA;

    /* Same as A but different instance */
    private Element elementNoDefaultsB;

    /* Test object with only defaults */
    private Element elementAllDefaults;

    /* Same as A but with null date */
    private Element elementNoDefaultsNullDateA;

    /* Same as null date A but different instance */
    private Element elementNoDefaultsNullDateB;

    /* Same as A but different date */
    private Element elementNoDefaultsDifferentDate;

    /* Class under test */
    private ReflectionComparator reflectionComparator, ignoreDefaultsReflectionComparator, lenientDatesReflectionComparator,
        ignoreDefaultsLenientDatesComparator;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void initialize()
        throws Exception {
        Date date = new Date();
        elementNoDefaultsA = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, date, "object");
        elementNoDefaultsB = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, date, "object");
        elementNoDefaultsNullDateA = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, null, "object");
        elementNoDefaultsNullDateB = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, null, "object");
        elementNoDefaultsDifferentDate = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, new Date(), "object");
        elementAllDefaults = new Element(false, (char) 0, (byte) 0, (short) 0, 0, 0l, 0.0f, 0.0, null, null);

        reflectionComparator = createRefectionComparator();
        ignoreDefaultsReflectionComparator = createRefectionComparator(IGNORE_DEFAULTS);
        lenientDatesReflectionComparator = createRefectionComparator(LENIENT_DATES);
        ignoreDefaultsLenientDatesComparator = createRefectionComparator(IGNORE_DEFAULTS, LENIENT_DATES);
    }

    /**
     * Test for two equal objects without java defaults.
     */
    @Test
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(elementNoDefaultsA, elementNoDefaultsB);
        assertNull(result);
    }

    /**
     * Test with left object containing only java defaults.
     */
    @Test
    public void equalsIgnoreDefaults() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(elementAllDefaults, elementNoDefaultsA);
        assertNull(result);
    }

    /**
     * Test with ignore defaults and left object null.
     */
    @Test
    public void equalsIgnoreDefaultsLeftNull() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(null, elementNoDefaultsA);
        assertNull(result);
    }

    /**
     * Test with ignore defaults and right object null
     */
    @Test
    public void notEqualsIgnoreDefaultsRightNull() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(elementNoDefaultsA, null);

        assertSame(elementNoDefaultsA, result.getLeftValue());
        assertNull(result.getRightValue());
    }

    /**
     * Test with ignore defaults and left value 0.
     */
    @Test
    public void equalsIgnoreDefaultsLeft0() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(0, 999);
        assertNull(result);
    }

    /**
     * Test with ignore defaults and right value 0.
     */
    @Test
    public void testGetDifference_equalsIgnoreDefaultsRight0() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(999, 0);

        assertEquals(999, result.getLeftValue());
        assertEquals(0, result.getRightValue());
    }

    /**
     * Test for lenient dates with 2 null dates.
     */
    @Test
    public void equalsLenientDatesBothNull() {
        Difference result = lenientDatesReflectionComparator.getDifference(elementNoDefaultsNullDateA, elementNoDefaultsNullDateB);
        assertNull(result);
    }

    /**
     * Test for lenient dates with 2 not null dates.
     */
    @Test
    public void equalsLenientDatesBothNotNull() {
        Difference result = lenientDatesReflectionComparator.getDifference(elementNoDefaultsA, elementNoDefaultsDifferentDate);
        assertNull(result);
    }

    /**
     * Test with left object containing only java defaults but no ignore defaults.
     */
    @Test
    public void notEqualsNoIgnoreDefaults() {
        Difference result = reflectionComparator.getDifference(elementAllDefaults, elementNoDefaultsB);

        Difference difference = getInnerDifference("booleanValue", result);
        assertEquals(Boolean.FALSE, difference.getLeftValue());
        assertEquals(Boolean.TRUE, difference.getRightValue());
    }

    /**
     * Test with right instead of left object containing only java defaults.
     */
    @Test
    public void notEqualsIgnoreDefaultsButDefaultsRight() {
        Difference result = ignoreDefaultsReflectionComparator.getDifference(elementNoDefaultsB, elementAllDefaults);

        Difference difference = getInnerDifference("booleanValue", result);
        assertEquals(Boolean.TRUE, difference.getLeftValue());
        assertEquals(Boolean.FALSE, difference.getRightValue());
    }

    /**
     * Test for lenient dates but with only right date null.
     */
    @Test
    public void notEqualsLenientDatesRightDateNull() {
        Difference result = lenientDatesReflectionComparator.getDifference(elementNoDefaultsDifferentDate, elementNoDefaultsNullDateA);

        Difference difference = getInnerDifference("dateValue", result);
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), difference.getLeftValue());
        assertNull(difference.getRightValue());
    }

    /**
     * Test for lenient dates but with only left date null.
     */
    @Test
    public void notEqualsLenientDatesLeftDateNull() {
        Difference result = lenientDatesReflectionComparator.getDifference(elementNoDefaultsNullDateA, elementNoDefaultsDifferentDate);

        Difference difference = getInnerDifference("dateValue", result);
        assertNull(difference.getLeftValue());
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), difference.getRightValue());
    }

    /**
     * Test for lenient dates while ignore defaults but with only left date null (= not treated as default).
     */
    @Test
    public void notEqualsLenientDatesAndIgnoreDefaultsWithLeftDateNull() {
        Difference result = ignoreDefaultsLenientDatesComparator.getDifference(elementNoDefaultsNullDateA, elementNoDefaultsDifferentDate);

        Difference difference = getInnerDifference("dateValue", result);
        assertNull(difference.getLeftValue());
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), difference.getRightValue());
    }

    /**
     * Test class with failing equals.
     */
    private class Element {

        /* A boolean value */
        private boolean booleanValue;

        /* A char value */
        private char charValue;

        /* A byte value */
        private byte byteValue;

        /* A short value */
        private short shortValue;

        /* An int value */
        private int intValue;

        /* A long value */
        private long longValue;

        /* A float value */
        private float floatValue;

        /* A double value */
        private double doubleValue;

        /* A date value */
        private Date dateValue;

        /* An object value */
        private Object objectValue;

        /**
         * Creates and initializes the element.
         *
         * @param booleanValue
         *     a boolean value
         * @param charValue
         *     a char value
         * @param byteValue
         *     a byte value
         * @param shortValue
         *     a short value
         * @param intValue
         *     an int value
         * @param longValue
         *     a long value
         * @param floatValue
         *     a float value
         * @param doubleValue
         *     a double value
         * @param dateValue
         *     a date value
         * @param objectValue
         *     an object value
         */
        public Element(boolean booleanValue, char charValue, byte byteValue, short shortValue, int intValue, long longValue, float floatValue,
            double doubleValue, Date dateValue, Object objectValue) {
            this.booleanValue = booleanValue;
            this.charValue = charValue;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
            this.longValue = longValue;
            this.floatValue = floatValue;
            this.doubleValue = doubleValue;
            this.dateValue = dateValue;
            this.objectValue = objectValue;
        }

        /**
         * Gets the boolean value.
         *
         * @return the boolean value
         */
        @UsedForTesting
        public boolean isBooleanValue() {
            return booleanValue;
        }

        /**
         * Gets the char value.
         *
         * @return the char value
         */
        @UsedForTesting
        public char getCharValue() {
            return charValue;
        }

        /**
         * Gets the byte value.
         *
         * @return the byte value
         */
        @UsedForTesting
        public byte getByteValue() {
            return byteValue;
        }

        /**
         * Gets the short value.
         *
         * @return the short value
         */
        @UsedForTesting
        public short getShortValue() {
            return shortValue;
        }

        /**
         * Gets the int value.
         *
         * @return the int value
         */
        @UsedForTesting
        public int getIntValue() {
            return intValue;
        }

        /**
         * Gets the long value.
         *
         * @return the long value
         */
        @UsedForTesting
        public long getLongValue() {
            return longValue;
        }

        /**
         * Gets the float value.
         *
         * @return the float value
         */
        @UsedForTesting
        public float getFloatValue() {
            return floatValue;
        }

        /**
         * Gets the double value.
         *
         * @return the double value
         */
        @UsedForTesting
        public double getDoubleValue() {
            return doubleValue;
        }

        /**
         * Gets the date value.
         *
         * @return the date value
         */
        public Date getDateValue() {
            return dateValue;
        }

        /**
         * Gets the object value.
         *
         * @return the object value
         */
        @UsedForTesting
        public Object getObjectValue() {
            return objectValue;
        }

        /**
         * Always returns false
         *
         * @param o
         *     the object to compare to
         */
        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
