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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.matcher.ElementMatchers;

import static java.lang.reflect.Modifier.isPublic;

import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Utility class to create and work with proxy objects.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ProxyFactory {
    private static Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

    public static final String PROXY_METHOD_INTERCEPTOR_FIELD_NAME = "$ProxyMethodInterceptor";

    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     * If possible, the default constructor (can be private) will be used. If there is no default constructor,
     * no constructor will be called.
     *
     * @param mockName
     *     The name of the mock, not null
     * @param invocationHandler
     *     The handler that will handle the method invocations of the proxy, not null.
     * @param proxiedClass
     *     The type to proxy, not null
     * @param implementedInterfaces
     *     Additional interfaces that the proxy must implement
     * @return The proxy object, not null
     */
    public static <T> T createProxy(String mockName, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass, Class<?>... implementedInterfaces) {
        return createProxy(mockName, true, invocationHandler, proxiedClass, implementedInterfaces);
    }

    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     * No constructor or class-initialization will be called.
     *
     * @param mockName
     *     The name of the mock, not null
     * @param invocationHandler
     *     The handler that will handle the method invocations of the proxy, not null.
     * @param proxiedClass
     *     The type to proxy, not null
     * @param implementedInterfaces
     *     Additional interfaces that the proxy must implement
     * @return The proxy object, not null
     */
    public static <T> T createUninitializedProxy(String mockName, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass,
        Class<?>... implementedInterfaces) {
        return createProxy(mockName, false, invocationHandler, proxiedClass, implementedInterfaces);
    }

    /**
     * Creates a proxy object for the given type. All method invocations will be passed to the given invocation handler.
     *
     * @param mockName
     *     The name of the mock, not null
     * @param initialize
     *     If possible, use the default constructor and initialize all fields
     * @param implementedInterfaces
     *     Additional interfaces that the proxy must implement
     * @param proxiedClass
     *     The type to proxy, not null
     * @param invocationHandler
     *     The handler that will handle the method invocations of the proxy, not null.
     * @return The proxy object, not null
     */
    protected static <T> T createProxy(String mockName, boolean initialize, ProxyInvocationHandler invocationHandler, Class<T> proxiedClass,
        Class<?>... implementedInterfaces) {
        ByteBuddyProxyMethodInterceptor proxyMethodInterceptor = new ByteBuddyProxyMethodInterceptor(mockName, proxiedClass, invocationHandler);
        Class<T> enhancedClass = createEnhancedClass(proxiedClass, implementedInterfaces, proxyMethodInterceptor);

        T proxy;
        if (initialize && !proxiedClass.isInterface()) {
            proxy = createInitializedOrUninitializedInstanceOfType(enhancedClass);
        } else {
            proxy = createUninitializedInstanceOfType(enhancedClass);
        }
        Field proxyMethodInterceptorField = ReflectionUtils.getFieldWithName(enhancedClass, PROXY_METHOD_INTERCEPTOR_FIELD_NAME, false);
        ReflectionUtils.setFieldValue(proxy, proxyMethodInterceptorField, proxyMethodInterceptor);
        return proxy;
    }

    /**
     * Creates an instance of the given type. First we try to create an instance using the default constructor.
     * If this doesn't work, eg if there is no default constructor, we try using objenesis. This way the class doesn't
     * have to offer an empty constructor in order for this method to succeed.
     *
     * @param <T>
     *     The type of the instance
     * @param clazz
     *     The class for which an instance is requested
     * @return An instance of the given class
     */
    public static <T> T createInitializedOrUninitializedInstanceOfType(Class<T> clazz) {
        try {
            return createInstanceOfType(clazz, true);
        } catch (UnitilsException e) {
            logger.warn("Could not create initialized instance of type " + clazz.getSimpleName()
                + ". No no-arg constructor found. All fields in the instance will have the java default values. Add a default constructor (can be private) if the fields should be initialized. If this concerns an innerclass, make sure it is declared static. Partial mocking of non-static innerclasses is not supported.",
                e);
        }
        // unable to create type using regular constuctor, try objenesis
        return createUninitializedInstanceOfType(clazz);
    }

    /**
     * Creates an instance of the given type. First we try to create an instance using the default constructor.
     * No constructor or class-initialization will be called.
     *
     * @param <T>
     *     The type of the instance
     * @param clazz
     *     The class for which an instance is requested
     * @return An instance of the given class
     */
    public static <T> T createUninitializedInstanceOfType(Class<T> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        return objenesis.newInstance(clazz);
    }

    protected static <T> Class<T> createEnhancedClass(Class<T> proxiedClass, Class<?>[] implementedInterfaces,
        ByteBuddyProxyMethodInterceptor proxyMethodInterceptor) {
        ByteBuddy byteBuddy = new ByteBuddy();

        DynamicType.Builder builder;
        if (proxiedClass.isInterface()) {
            byteBuddy = byteBuddy
                .with(new NamingStrategy.SuffixingRandom("ByteBuddy", new NamingStrategy.Suffixing.BaseNameResolver.ForFixedValue(proxiedClass.getName())));
            builder = byteBuddy.subclass(Object.class);
            builder = builder.implement(proxiedClass);
        } else {
            builder = byteBuddy.subclass(proxiedClass);
        }
        if (implementedInterfaces != null && implementedInterfaces.length > 0) {
            builder = builder.implement(implementedInterfaces);
        }
        builder = builder.defineField(PROXY_METHOD_INTERCEPTOR_FIELD_NAME, ByteBuddyProxyMethodInterceptor.class, Visibility.PUBLIC);
        builder = builder.method(ElementMatchers.any()).intercept(MethodDelegation.withEmptyConfiguration().withResolvers(MethodNameResolver.INSTANCE)
            .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS).to(proxyMethodInterceptor));
        return builder.make().load(getClassLoader(proxiedClass), getClassLoadingStrategy(proxiedClass)).getLoaded();
    }

    enum MethodNameResolver
        implements MethodDelegationBinder.AmbiguityResolver {
        /**
         * The singleton instance.
         */
        INSTANCE;

        /**
         * {@inheritDoc}
         */
        @Override
        public Resolution resolve(MethodDescription source, MethodDelegationBinder.MethodBinding left, MethodDelegationBinder.MethodBinding right) {
            boolean leftEquals = left.getTarget().getName().equals("intercept");
            boolean rightEquals = right.getTarget().getName().equals("intercept");
            if (leftEquals ^ rightEquals) {
                return leftEquals ? Resolution.LEFT : Resolution.RIGHT;
            } else {
                return Resolution.UNKNOWN;
            }
        }
    }

    private static ClassLoader getClassLoader(Class proxiedClass) {
        ClassLoader classLoader = proxiedClass.getClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = ProxyFactory.class.getClassLoader();
        }
        if (classLoader == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return classLoader;
    }

    private static ClassLoadingStrategy getClassLoadingStrategy(Class proxiedClass) {
        if (!isPublic(proxiedClass.getModifiers()) && ClassInjector.UsingLookup.isAvailable()) {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Method privateLookupIn = ReflectionUtils.getMethod(MethodHandles.class, "privateLookupIn", true, Class.class, MethodHandles.Lookup.class);
            if (privateLookupIn == null) {
                throw new IllegalStateException("No code generation strategy available");
            }
            Object privateLookup;
            try {
                privateLookup = ReflectionUtils.invokeMethod(null, privateLookupIn, proxiedClass, lookup);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("No code generation strategy available", e.getTargetException());
            }
            return ClassLoadingStrategy.UsingLookup.of(privateLookup);
        } else if (ClassInjector.UsingReflection.isAvailable()) {
            return ClassLoadingStrategy.Default.INJECTION;
        } else {
            throw new IllegalStateException("No code generation strategy available");
        }
    }
}
