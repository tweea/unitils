/*
 * Copyright 2011,  Unitils.org
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

package org.unitilsnew.core.config;

import org.unitils.core.UnitilsException;
import org.unitilsnew.core.Factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * todo javadoc
 *
 * If classifiers are used, it will first look for a property using all classifiers, then look for
 * a property without the last classifier etc. If still no property was found, it will look for the property
 * without discriminators. E.g. suppose the property name is 'key' and there are 2 classifiers 'a' and 'b'. First
 * it will look for a property with name 'key.a.b', if that doesn't exist it will look for 'key.a', and
 * finally it will try 'key'.
 *
 * @author Tim Ducheyne
 */
public class Configuration {

    /* All configuration properties, not null */
    private Properties properties;


    /**
     * Creates a configuration for the given properties.
     *
     * @param properties All configuration properties, not null
     */
    public Configuration(Properties properties) {
        this.properties = properties;
    }


    /**
     * @param propertyName The property name, not null
     * @return True if the property exists
     */
    public boolean containsProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    /**
     * @return All properties, not null
     */
    public Properties getProperties() {
        return properties;
    }


    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string value, not null
     */
    public String getString(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        if (value == null) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return value;
    }

    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, null is returned.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string value, null if not found
     */
    public String getOptionalString(String propertyName, String... classifiers) {
        String value = properties.getProperty(propertyName);

        if (classifiers != null && classifiers.length > 0) {
            StringBuilder propertyNameWithClassifiers = new StringBuilder(propertyName);
            for (String classifier : classifiers) {
                propertyNameWithClassifiers.append('.');
                propertyNameWithClassifiers.append(classifier.trim());

                String valueForClassifier = properties.getProperty(propertyNameWithClassifiers.toString());
                if (valueForClassifier != null) {
                    value = valueForClassifier;
                }
            }
        }

        if (value == null) {
            return null;
        }

        value = value.trim();
        if ("".equals(value)) {
            return null;
        }
        return value;
    }


    /**
     * Gets the boolean value for the property with the given name. If no such property is found,
     * the value is empty or not a boolean, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean value, not null
     */
    public Boolean getBoolean(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toBoolean(value, propertyName, classifiers);
    }

    /**
     * Gets the boolean value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value is not a boolean.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean value, null if not found
     */
    public Boolean getOptionalBoolean(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toBoolean(value, propertyName, classifiers);
    }


    /**
     * Gets the int value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to an int, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int value
     */
    public Integer getInteger(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toInteger(value, propertyName, classifiers);
    }

    /**
     * Gets the int value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value cannot be converted to an int.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int value, null if not found
     */
    public Integer getOptionalInteger(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toInteger(value, propertyName, classifiers);
    }


    /**
     * Gets the long value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to a long, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The long value, not null
     */
    public Long getLong(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toLong(value, propertyName, classifiers);
    }

    /**
     * Gets the long value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value cannot be converted to a long.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The long value, null if not found
     */
    public Long getOptionalLong(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toLong(value, propertyName, classifiers);
    }


    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string list, not null
     */
    public List<String> getStringList(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        List<String> result = toStringList(value);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string list, empty if not found
     */
    public List<String> getOptionalStringList(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toStringList(value);
    }


    /**
     * Gets an instance of the class name specified by the property with the given name. If no such property is found, the
     * value is empty or the instance cannot be created, an exception will be raised.<br/>
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance value, not null
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getInstance(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return (T) toInstance(value, propertyName, classifiers);
    }

    /**
     * Gets an instance of the class name specified by the property with the given name. If no such property is found, the
     * value is empty, null is returned. An exception will be raised if the instance cannot be created.<br/
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance value, null if not found
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getOptionalInstance(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return (T) toInstance(value, propertyName, classifiers);
    }

    /**
     * Gets an instance of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance
     */
    public <T> T getInstanceOf(Class<T> type, String... classifiers) {
        String propertyName = type.getName();
        String value = getString(propertyName, classifiers);
        return toInstance(type, value, propertyName, classifiers);
    }

    /**
     * Gets an instance of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance
     */
    public <T> T getOptionalInstanceOf(Class<T> type, String... classifiers) {
        String propertyName = type.getName();
        String value = getOptionalString(type.getName(), classifiers);
        return toInstance(type, value, propertyName, classifiers);
    }


    public <T extends Enum<T>> T getEnumValue(Class<T> type, String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toEnum(type, value, propertyName, classifiers);
    }

    public <T extends Enum<T>> T getOptionalEnumValue(Class<T> type, String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toEnum(type, value, propertyName, classifiers);
    }


    public <T> T getValueOfType(Class<T> type, String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toValueOfType(type, value, propertyName, classifiers);
    }

    public <T> T getOptionalValueOfType(Class<T> type, String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toValueOfType(type, value, propertyName, classifiers);
    }


    public <T> List<T> getValueListOfType(Class<T> type, String propertyName, String... classifiers) {
        List<String> values = getStringList(propertyName, classifiers);

        List<T> result = new ArrayList<T>(values.size());
        for (String value : values) {
            T valueOfType = toValueOfType(type, value, propertyName, classifiers);
            result.add(valueOfType);
        }
        return result;
    }

    public <T> List<T> getOptionalValueListOfType(Class<T> type, String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<T> result = new ArrayList<T>(values.size());
        for (String value : values) {
            T valueOfType = toValueOfType(type, value, propertyName, classifiers);
            result.add(valueOfType);
        }
        return result;
    }


    protected String nameToString(String propertyName, String... classifiers) {
        if (classifiers == null || classifiers.length == 0) {
            return "property " + propertyName;
        }
        return "property " + propertyName + " and classifiers " + Arrays.toString(classifiers);
    }


    protected Boolean toBoolean(String value, String propertyName, String... classifiers) {
        if (value == null) {
            return null;
        }
        if ("true".equalsIgnoreCase(value)) {
            return TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return FALSE;
        }
        throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a boolean.");
    }

    protected Integer toInteger(String value, String propertyName, String... classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Integer.valueOf(value);

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not an int.");
        }
    }

    protected Long toLong(String value, String propertyName, String... classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Long.valueOf(value);

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a long.");
        }
    }

    protected List<String> toStringList(String value) {
        if (value == null) {
            return new ArrayList<String>(0);
        }
        String[] splitValues = value.split(",");
        List<String> result = new ArrayList<String>(splitValues.length);
        for (String splitValue : splitValues) {
            splitValue = splitValue.trim();
            if ("".equals(splitValue)) {
                continue;
            }
            result.add(splitValue);
        }
        return result;
    }

    protected <T extends Enum<T>> T toEnum(Class<T> type, String value, String propertyName, String[] classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Enum.valueOf(type, value);
        } catch (Exception e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a valid enum value for type " + type.getName(), e);
        }
    }


    @SuppressWarnings({"unchecked"})
    protected <T> T toInstance(String value, String propertyName, String... classifiers) {
        if (value == null) {
            return null;
        }
        try {
            T instance = (T) createInstanceOfType(value, true);
            if (instance instanceof Factory) {
                return (T) ((Factory) instance).create();
            }
            return instance;
        } catch (Exception e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a valid classname.", e);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected <T> T toInstance(Class<T> type, String value, String propertyName, String... classifiers) {
        T instance = (T) toInstance(value, propertyName, classifiers);
        if (instance != null && !type.isAssignableFrom(instance.getClass())) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not of the expected type " + type.getName());
        }
        return instance;
    }

    @SuppressWarnings({"unchecked"})
    protected <T> T toValueOfType(Class<T> type, String value, String propertyName, String... classifiers) {
        if (type.isAssignableFrom(String.class)) {
            return (T) value;
        }
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            return (T) toBoolean(value, propertyName, classifiers);
        }
        if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            return (T) toInteger(value, propertyName, classifiers);
        }
        if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            return (T) toLong(value, propertyName, classifiers);
        }
        if (type.isEnum()) {
            return (T) toEnum((Class<Enum>) type, value, propertyName, classifiers);
        }
        return toInstance(type, value, propertyName, classifiers);
    }
}
