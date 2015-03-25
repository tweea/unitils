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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for defining that a value is a default value for an annotation. This is used in combination with
 * {@code Annotations.getAnnotationWithDefaults()}.<br/>
 * <br/>
 * The same annotation can be placed on several levels: method/field, class, parent class etc. If you request an annotation
 * with defaults, a merged annotation is returned starting from the lowest level going up to the higher levels.
 * Default values will be replaced by non-default values.<br/>
 * <br/>
 * You can specify defaults for annotation values, but there is no way to distinguish that value from a real value.
 * This annotation will mark the value as default value.<br/><br/>
 * <pre>
 *     {@code @AnnotationDefault String myValue() default "something";}
 * </pre>
 * If the value remains (or is set to) "something", it is treated as a default. If a there is a higher level annotation (e.g. class or parent class)
 * that has a real value (not "something") that value will be used.<br/>
 * <br/>
 * You can also specify a property name. If no value was specified on any level, the value of the property will be returned.<br/><br/>
 * <pre>
 *      {@code @AnnotationDefault("my-default-property") String myValue() default "something";}
 * </pre>
 * If no property name was specified, the default value itself will be returned.
 *
 * @author Tim Ducheyne
 * @see org.unitils.core.reflect.Annotations#getAnnotationWithDefaults
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationDefault {

    /**
     * @return The name of a property for looking up the value if no non-default value was provided.
     * If no property is defined, the default value of the annotation is returned.
     */
    String value() default "";
}
