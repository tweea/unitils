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

import java.lang.reflect.Method;
import java.util.List;

/**
 * A method invocation matcher that uses a given <code>Method</code> and a list of {@link ArgumentMatcher}s to match an executed <code>Method</code> and its parameters. 
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InvocationMatcher {
	
	private final Method method;
	private final List<ArgumentMatcher> argumentMatchers;
	
	/**
	 * Constructor.
	 * 
	 * @param method The <code>Method</code> which needs to be matched.
	 * @param argumentMatchers The {@link ArgumentMatcher}s that need to be used to match the {@link Invocation}s arguments.
	 */
	public InvocationMatcher(Method method, List<ArgumentMatcher> argumentMatchers) {
		this.method = method;
		this.argumentMatchers = argumentMatchers;
	}
	
	/**
	 * Returns whether or not the given {@link Invocation} matches this object's predefined <code>Method</code> and arguments.
	 * @param invocation the {@link Invocation} to match.
	 * @return true when given {@link Invocation} matches, false otherwise.
	 */
	public boolean matches(Invocation invocation) {
		if(!this.method.equals(invocation.getMethod())) {
			return false;
		}
		final List<?> arguments = invocation.getArguments();
		if(arguments.size() != argumentMatchers.size()) {
			return false;
		}
		for(int i = 0; i < arguments.size(); ++i) {
			if(!argumentMatchers.get(i).matches(arguments.get(i))) {
				return false;
			}
		}
		return true;
	}
}
