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

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.unitils.core.UnitilsException;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Arrays.asList;

import static org.unitils.mock.core.proxy.ProxyUtils.getProxiedMethodStackTrace;
import static org.unitils.util.MethodUtils.isCloneMethod;
import static org.unitils.util.MethodUtils.isEqualsMethod;
import static org.unitils.util.MethodUtils.isFinalizeMethod;
import static org.unitils.util.MethodUtils.isHashCodeMethod;
import static org.unitils.util.MethodUtils.isToStringMethod;

/**
 * A ByteBuddy method intercepter that will delegate the invocations to the given invocation hanlder.
 */
public class ByteBuddyProxyMethodInterceptor<T> {
    private String mockName;

    private Class<T> proxiedType;

    /* The invocation handler */
    private ProxyInvocationHandler invocationHandler;

    /**
     * Creates an interceptor.
     *
     * @param mockName
     *     The name of the mock, not null
     * @param proxiedType
     *     The proxied type, not null
     * @param invocationHandler
     *     The handler to delegate the invocations to, not null
     */
    public ByteBuddyProxyMethodInterceptor(String mockName, Class<T> proxiedType, ProxyInvocationHandler invocationHandler) {
        this.mockName = mockName;
        this.proxiedType = proxiedType;
        this.invocationHandler = invocationHandler;
    }

    /**
     * Intercepts the method call by wrapping the invocation in a {@link ByteBuddyProxyInvocation} and delegating the
     * handling to the invocation handler.
     *
     * @param proxy
     *     The proxy, not null
     * @param method
     *     The method that was called, not null
     * @param arguments
     *     The arguments that were used, may null
     * @param superMethod
     *     The original method that was called, may null
     * @return The value to return for the method call, ignored for void methods
     */
    @RuntimeType
    public Object intercept(@This Object proxy, @Origin Method method, @AllArguments Object[] arguments,
        @SuperMethod(nullIfImpossible = true) Method superMethod)
        throws Throwable {
        if (arguments == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        if (isFinalizeMethod(method)) {
            return null;
        } else if (isEqualsMethod(method)) {
            return proxy == arguments[0];
        } else if (isHashCodeMethod(method)) {
            return super.hashCode();
        } else if (isCloneMethod(method)) {
            return proxy;
        } else if (isToStringMethod(method)) {
            return getProxiedType().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
        }

        ProxyInvocation invocation = new ByteBuddyProxyInvocation(mockName, method, asList(arguments), getProxiedMethodStackTrace(), proxy, superMethod);
        Object result = invocationHandler.handleInvocation(invocation);
        if (result == null) {
            // prevent NPE when autounboxing the primitive types
            result = getDefaultValue(method.getReturnType());
        }
        return result;
    }

    private Object getDefaultValue(Class<?> returnType) {
        if (returnType.isPrimitive()) {
            if (returnType == Boolean.TYPE) {
                return Boolean.FALSE;
            } else if (returnType == Character.TYPE) {
                return Character.valueOf(CharUtils.NUL);
            } else if (returnType == Byte.TYPE) {
                return NumberUtils.BYTE_ZERO;
            } else if (returnType == Short.TYPE) {
                return NumberUtils.SHORT_ZERO;
            } else if (returnType == Integer.TYPE) {
                return NumberUtils.INTEGER_ZERO;
            } else if (returnType == Long.TYPE) {
                return NumberUtils.LONG_ZERO;
            } else if (returnType == Float.TYPE) {
                return NumberUtils.FLOAT_ZERO;
            } else if (returnType == Double.TYPE) {
                return NumberUtils.DOUBLE_ZERO;
            }
        }
        return null;
    }

    public String getMockName() {
        return mockName;
    }

    /**
     * @return The proxied type, not null
     */
    public Class<?> getProxiedType() {
        return proxiedType;
    }

    /**
     * An invocation implementation that uses the ByteBuddy method proxy to be able to invoke the original behavior.
     */
    public static class ByteBuddyProxyInvocation
        extends ProxyInvocation {

        /* The original method that was called, may null */
        private Method superMethod;

        /**
         * Creates an invocation.
         *
         * @param mockName
         *     The name of the mock, not null
         * @param method
         *     The method that was called, not null
         * @param arguments
         *     The arguments that were used, not null
         * @param invokedAt
         *     The location of the invocation, not null
         * @param proxy
         *     The proxy, not null
         * @param superMethod
         *     The original method that was called, may null
         */
        public ByteBuddyProxyInvocation(String mockName, Method method, List<Object> arguments, StackTraceElement[] invokedAt, Object proxy,
            Method superMethod) {
            super(mockName, proxy, method, arguments, invokedAt);
            this.superMethod = superMethod;
        }

        /**
         * Invokes the original behavior by calling the method proxy.
         * If there is no original behavior, e.g. an interface or abstract method, an exception is raised.
         *
         * @return The result value
         */
        @Override
        public Object invokeOriginalBehavior()
            throws Throwable {
            if (superMethod == null || isAbstract(superMethod.getModifiers())) {
                throw new UnitilsException("Unable to invoke original behavior. The method is abstract, it does not have any behavior defined: " + getMethod());
            }
            return superMethod.invoke(getProxy(), getArguments().toArray());
        }
    }
}
