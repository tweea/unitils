/*
 * Copyright 2006-2007,  Unitils.org
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockObject<T> {

	private Scenario scenario;
	
	private Action defaultAction;
	
	private Class<T> mockedClass;
	
	private List<MockBehavior> alwaysMatchingMockBehaviors = new ArrayList<MockBehavior>();
	
	private Map<MockBehavior, Boolean> oneTimeMatchingMockBehaviors = new LinkedHashMap<MockBehavior, Boolean>();
	

	public MockObject(Scenario scenario, Action defaultAction, Class<T> mockedClass) {
		super();
		this.scenario = scenario;
		this.defaultAction = defaultAction;
		this.mockedClass = mockedClass;
	}

	
	public Object handleInvocation(Invocation invocation) throws Throwable {
		scenario.registerInvocation(invocation);
		return executeBehavior(invocation);
	}

	
	protected Object executeBehavior(Invocation invocation) throws Throwable {
		// Check if there is a one-time matching behavior that hasn't been invoked yet
		for (MockBehavior behavior : oneTimeMatchingMockBehaviors.keySet()) {
			if (!oneTimeMatchingMockBehaviors.get(behavior) && behavior.matches(invocation)) {
				// Register the fact that this behavior has been matched, so that is not matched a second time anymore
				oneTimeMatchingMockBehaviors.put(behavior, true);
				return behavior.execute(invocation);
			}
		}
		// Check if there is an always-matching behavior
		for (MockBehavior behavior : alwaysMatchingMockBehaviors) {
			if (behavior.matches(invocation)) {
				return behavior.execute(invocation);
			}
		}
		// There's no matching behavior, simply execute the default one
		return defaultAction.execute(invocation);                                       
	}
	
	
	public void registerAlwaysMatchingMockBehavior(MockBehavior mockBehavior) {
		alwaysMatchingMockBehaviors.add(mockBehavior);
		scenario.registerAlwaysMatchingMockBehaviorInvocationMatcher(mockBehavior.getInvocationMatcher());
	}
	
	
	public void registerOneTimeMatchingMockBehavior(MockBehavior mockBehavior) {
		oneTimeMatchingMockBehaviors.put(mockBehavior, false);
		scenario.registerOneTimeMatchingMockBehaviorInvocationMatcher(mockBehavior.getInvocationMatcher());
	}
	
	
	public List<MockBehavior> getAlwaysMatchingMockBehaviors() {
		return alwaysMatchingMockBehaviors;
	}


	public List<MockBehavior> getOneTimeMatchingMockBehaviors() {
		return new ArrayList<MockBehavior>(oneTimeMatchingMockBehaviors.keySet());
	}


	public Class<T> getMockedClass() {
		return mockedClass;
	}

}