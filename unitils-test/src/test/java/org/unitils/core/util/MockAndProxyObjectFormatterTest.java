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
package org.unitils.core.util;

import java.util.Collection;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;
import org.unitils.util.ReflectionUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;

import static org.junit.Assert.assertEquals;

/**
 * Tests the formatting of proxies and mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockAndProxyObjectFormatterTest
    extends UnitilsJUnit4 {
    private ObjectFormatter objectFormatter = new ObjectFormatter();

    @Test
    public void formatByteBuddyProxy() {
        Object proxy = createByteBuddyProxy();
        String result = objectFormatter.format(proxy);
        assertEquals("Proxy<Collection>", result);
    }

    @Test
    public void formatMock() {
        Mock<Collection> mock = new MockObject<>("mockName", Collection.class, this);
        String result = objectFormatter.format(mock);
        assertEquals("Mock<mockName>", result);
    }

    @Test
    public void formatMockProxy() {
        Object mockProxy = new MockObject<Collection>("mockName", Collection.class, this).getMock();
        String result = objectFormatter.format(mockProxy);
        assertEquals("Mock<mockName>", result);
    }

    private Object createByteBuddyProxy() {
        ByteBuddy byteBuddy = new ByteBuddy();
        byteBuddy = byteBuddy
            .with(new NamingStrategy.SuffixingRandom("ByteBuddy", new NamingStrategy.Suffixing.BaseNameResolver.ForFixedValue(Collection.class.getName())));
        DynamicType.Builder builder = byteBuddy.subclass(Object.class);
        builder = builder.implement(Collection.class);
        Class type = builder.make().load(getClass().getClassLoader()).getLoaded();
        return ReflectionUtils.createInstanceOfType(type, false);
    }
}
