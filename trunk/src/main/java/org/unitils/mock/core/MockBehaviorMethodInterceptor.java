/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock.core;

import org.unitils.mock.syntax.MockBehaviorBuilder;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockBehaviorMethodInterceptor<T> extends BaseMethodInterceptor<T> {

    private MockBehaviorBuilder mockBehaviorBuilder = MockBehaviorBuilder.getInstance();


    public MockBehaviorMethodInterceptor(MockObject<T> mockObject, Scenario scenario) {
        super(mockObject, scenario);
    }


    public Object handleInvocation(Invocation invocation) throws Throwable {
        mockBehaviorBuilder.registerInvokedMethod(invocation);
        return null;
    }

}
