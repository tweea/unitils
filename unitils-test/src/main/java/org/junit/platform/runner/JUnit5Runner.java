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
package org.junit.platform.runner;

import java.util.List;
import java.util.Set;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.suite.commons.SuiteLauncherDiscoveryRequestBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

import static java.util.stream.Collectors.toList;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.suite.commons.SuiteLauncherDiscoveryRequestBuilder.request;

/**
 * JUnit 4 based {@link Runner} which runs tests on the JUnit Platform in a
 * JUnit 4 environment.
 */
public class JUnit5Runner
    extends Runner
    implements Filterable {
    private final Class<?> testClass;

    private final Launcher launcher;

    private JUnitPlatformTestTree testTree;

    public JUnit5Runner(Class<?> testClass) {
        this.testClass = testClass;
        this.launcher = LauncherFactory.create();
        this.testTree = generateTestTree(createDiscoveryRequest());
    }

    @Override
    public Description getDescription() {
        return this.testTree.getSuiteDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        this.launcher.execute(this.testTree.getTestPlan(), new JUnitPlatformRunnerListener(this.testTree, notifier));
    }

    private JUnitPlatformTestTree generateTestTree(LauncherDiscoveryRequest discoveryRequest) {
        TestPlan testPlan = this.launcher.discover(discoveryRequest);
        return new JUnitPlatformTestTree(testPlan, this.testClass);
    }

    private LauncherDiscoveryRequest createDiscoveryRequest() {
        SuiteLauncherDiscoveryRequestBuilder requestBuilder = request();
        // Allows @RunWith(JUnitPlatform.class) to be added to any test case
        requestBuilder.selectors(selectClass(this.testClass));
        requestBuilder.applyConfigurationParametersFromSuite(testClass).applySelectorsAndFiltersFromSuite(this.testClass);
        return requestBuilder.build();
    }

    @Override
    public void filter(Filter filter)
        throws NoTestsRemainException {
        Set<TestIdentifier> filteredIdentifiers = this.testTree.getFilteredLeaves(filter);
        if (filteredIdentifiers.isEmpty()) {
            throw new NoTestsRemainException();
        }
        this.testTree = generateTestTree(createDiscoveryRequestForUniqueIds(filteredIdentifiers));
    }

    private LauncherDiscoveryRequest createDiscoveryRequestForUniqueIds(Set<TestIdentifier> testIdentifiers) {
        List<DiscoverySelector> selectors = testIdentifiers.stream().map(TestIdentifier::getUniqueIdObject).map(DiscoverySelectors::selectUniqueId)
            .collect(toList());
        return request().selectors(selectors).build();
    }
}
