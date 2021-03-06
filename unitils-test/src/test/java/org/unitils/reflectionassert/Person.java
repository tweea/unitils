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

import java.util.Arrays;
import java.util.List;

import org.unitils.core.annotation.UsedForTesting;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Person {
    @UsedForTesting
    private String fName;

    @UsedForTesting
    private String lName;

    @UsedForTesting
    private String userName;

    @UsedForTesting
    private List<Car> cars;

    public Person(String fName, String lName, String userName, Car... cars) {
        this.fName = fName;
        this.lName = lName;
        this.userName = userName;
        this.cars = Arrays.asList(cars);
    }
}
