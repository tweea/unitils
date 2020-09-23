/*
 * Copyright 2006-2007, Unitils.org
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
package org.unitils.mock.core;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectRaisesTest {
    /* Class under test */
    private MockObject<TestClass> mockObject;

    @Before
    public void setUp() {
        mockObject = new MockObject<>("testMock", TestClass.class, this);
    }

    /**
     * Tests setting an exception behavior for the mock. The behavior is an always matching behavior
     * so the method should keep throwing that same exception.
     */
    @Test
    public void raises() {
        mockObject.raises(new ThreadDeath()).testMethodString();

        ThreadDeath error1 = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), ThreadDeath.class);
        assertThat(error1).isNotNull();
        ThreadDeath error2 = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), ThreadDeath.class);
        assertThat(error2).isNotNull();
    }

    /**
     * Tests setting an once exception behavior for the mock. The behavior should be executed only once, the second time
     * no exception should be raised.
     */
    @Test
    public void onceRaises() {
        mockObject.onceRaises(new ThreadDeath()).testMethodString();

        ThreadDeath error1 = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), ThreadDeath.class);
        assertThat(error1).isNotNull();
        ThreadDeath error2 = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), ThreadDeath.class);
        assertThat(error2).isNull();
    }

    @Test
    public void raisesWithExceptionClass() {
        mockObject.raises(IllegalArgumentException.class).testMethodString();

        IllegalArgumentException exception = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), IllegalArgumentException.class);
        assertThat(exception).as("Expected exception").isNotNull();
    }

    @Test
    public void onceRaisesWithexceptionClass() {
        mockObject.onceRaises(IllegalArgumentException.class).testMethodString();

        IllegalArgumentException exception = catchThrowableOfType(() -> mockObject.getMock().testMethodString(), IllegalArgumentException.class);
        assertThat(exception).as("Expected exception").isNotNull();
    }

    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {
        public String testMethodString();
    }
}
