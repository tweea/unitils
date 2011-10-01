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
package org.unitils.dataset.annotation.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking an annotation as being an assert data set annotation.
 * <p/>
 * This can be used for creating your own assert data set annotation. Just put this annotation on your annotation and
 * define a handler for it that will handle the actual assertion. E.g.
 * <code><pre>
 * '@Target({TYPE, METHOD})
 * '@Retention(RUNTIME)
 * '@Inherited
 * '@MarkerForAssertDataSetAnnotation(MyAnnotationHandler.class)
 * public @interface MyAssertDataSetAnnotation {
 * ...
 * </pre></code>
 * <p/>
 * See {@link org.unitils.dataset.annotation.handler.DataSetAnnotationHandler} for more info on how to implement an annotation handler.
 */

@Target({TYPE})
@Retention(RUNTIME)
public @interface MarkerForAssertDataSetAnnotation {

    /**
     * @return The annotation handler that will do the actual assert operation, not null
     */
    Class<? extends DataSetAnnotationHandler> value();
}