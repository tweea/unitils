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

package org.unitils.mock.listener;

import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.core.TestPhase;
import org.unitils.mock.core.Scenario;

import static org.unitils.core.TestPhase.INITIALIZATION;

/**
 * @author Tim Ducheyne
 */
public class ResetStateTestListener extends TestListener {

    protected Scenario scenario;


    public ResetStateTestListener(Scenario scenario) {
        this.scenario = scenario;
    }


    @Override
    public TestPhase getTestPhase() {
        return INITIALIZATION;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        scenario.reset();
    }
}