/*
 * * Copyright 2010, Unitils.org
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 */
package org.unitils.mock.core.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.MockObject;
import org.unitils.util.ReflectionUtils;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyUtils {
    /**
     * @param object
     *     The object to check
     * @return The proxied type, null if the object is not a proxy
     */
    public static Class<?> getProxiedTypeIfProxy(Object object) {
        if (object == null) {
            return null;
        }
        if (isProxyClassName(object.getClass().getName())) {
            Field proxyMethodInterceptorField = ReflectionUtils.getFieldWithName(object.getClass(), ProxyFactory.PROXY_METHOD_INTERCEPTOR_FIELD_NAME, false);
            if (proxyMethodInterceptorField == null) {
                return null;
            }
            ByteBuddyProxyMethodInterceptor proxyMethodInterceptor = ReflectionUtils.getFieldValue(object, proxyMethodInterceptorField);
            if (proxyMethodInterceptor == null) {
                return null;
            }
            return proxyMethodInterceptor.getProxiedType();
        }
        return null;
    }

    /**
     * @param instance
     *     The instance to check, not null
     * @return True if the given instance is a jdk or ByteBuddy proxy
     */
    public static boolean isProxy(Object instance) {
        if (instance == null) {
            return false;
        }
        Class<?> clazz = instance.getClass();
        return isProxyClassName(clazz.getName()) || Proxy.isProxyClass(clazz);
    }

    /**
     * @param className
     *     The class name to check, not null
     * @return True if the given class name is ByteBuddy proxy class name
     */
    public static boolean isProxyClassName(String className) {
        return className.contains("$ByteBuddy$");
    }

    /**
     * note: don't remove, used through reflection from {@link org.unitils.core.util.ObjectFormatter}
     *
     * @param object
     *     The object to check
     * @return The proxied type, null if the object is not a proxy or mock
     */
    public static String getMockName(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof MockObject) {
            return ((MockObject) object).getName();
        }
        if (isProxyClassName(object.getClass().getName())) {
            Field proxyMethodInterceptorField = ReflectionUtils.getFieldWithName(object.getClass(), ProxyFactory.PROXY_METHOD_INTERCEPTOR_FIELD_NAME, false);
            if (proxyMethodInterceptorField == null) {
                return null;
            }
            ByteBuddyProxyMethodInterceptor proxyMethodInterceptor = ReflectionUtils.getFieldValue(object, proxyMethodInterceptorField);
            if (proxyMethodInterceptor == null) {
                return null;
            }
            return proxyMethodInterceptor.getMockName();
        }
        return null;
    }

    /**
     * First finds a trace element in which a ByteBuddy proxy method was invoked. Then it returns the rest of the stack trace following that
     * element. The stack trace starts with the element rh r is the method call that was proxied by the proxy method.
     *
     * @return The proxied method trace, not null
     */
    public static StackTraceElement[] getProxiedMethodStackTrace() {
        List<StackTraceElement> stackTrace = new ArrayList<>();

        boolean foundProxyMethod = false;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (foundProxyMethod) {
                stackTrace.add(stackTraceElement);
            } else if (isProxyClassName(stackTraceElement.getClassName())) {
                // found the proxy method element, the next element is the proxied method element
                foundProxyMethod = true;
            }
        }
        if (stackTrace.isEmpty()) {
            throw new UnitilsException("No invocation of a ByteBuddy proxy method found in stacktrace: " + Arrays.toString(stackTraceElements));
        }
        return stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
    }
}
