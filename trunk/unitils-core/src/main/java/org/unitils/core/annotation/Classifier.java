/*
 * Copyright 2013,  Unitils.org
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

package org.unitils.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A classifier can be used on a constructor argument to fine tune injection. One or more classifier names can be provided.<br/>
 * <br/>
 * If classifiers are used, it will first look for a property using all classifiers, then look for
 * a property without the last classifier etc. If still no property was found, it will look for the property
 * without discriminators. E.g. suppose the property name is 'key' and there are 2 classifiers 'a' and 'b'. First
 * it will look for a property with name 'key.a.b', if that doesn't exist it will look for 'key.a', and
 * finally it will try 'key'.<br/>
 * <br/>
 * Example usage:<br/>
 * <pre>
 *   {@code public MyDao(@Classifier({"schema1"}) DataSource schema1DataSource, @Classifier({"schema2"}) DataSource schema2DataSource)}
 * </pre>
 *
 * @author Tim Ducheyne
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Classifier {

    /**
     * @return The classifier name(s)
     */
    String[] value();

}
