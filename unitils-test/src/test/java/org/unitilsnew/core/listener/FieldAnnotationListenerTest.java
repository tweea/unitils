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

package org.unitilsnew.core.listener;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class FieldAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private FieldAnnotationListener<Target> fieldAnnotationListener = new MyFieldAnnotationListener<Target>();

    @Dummy
    private TestField testField;
    @Dummy
    private Annotations<Target> annotations;
    @Dummy
    private TestClass testClass;
    @Dummy
    private TestInstance testInstance;


    @Test
    public void testPhaseDefaultsToExecutionPhase() {
        TestPhase result = fieldAnnotationListener.getTestPhase();
        assertEquals(EXECUTION, result);
    }

    @Test
    public void defaultBeforeTestSetUpDoesNothing() {
        fieldAnnotationListener.beforeTestSetUp(testInstance, testField, annotations);
    }

    @Test
    public void defaultBeforeTestMethodDoesNothing() {
        fieldAnnotationListener.beforeTestMethod(testInstance, testField, annotations);
    }

    @Test
    public void defaultAfterTestMethodDoesNothing() {
        NullPointerException e = new NullPointerException();
        fieldAnnotationListener.afterTestMethod(testInstance, testField, annotations, e);
    }

    @Test
    public void defaultAfterTestTearDownDoesNothing() {
        fieldAnnotationListener.afterTestTearDown(testInstance, testField, annotations);
    }


    private static class MyFieldAnnotationListener<A extends Annotation> extends FieldAnnotationListener<A> {
    }
}
