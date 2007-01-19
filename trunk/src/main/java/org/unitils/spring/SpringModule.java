/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.spring;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A module for Spring enabling a test class by offering an easy way to load application contexts and
 * an easy way of retrieving beans from the context and injecting them in the test.
 * <p/>
 * The application context loading can be achieved by using the {@link SpringApplicationContext} annotation.
 * <p/>
 * Spring bean retrieval can be done by annotating the corresponding fields in the test with following
 * annotations: {@link SpringBean}, {@link SpringBeanByName} and {@link SpringBeanByType}.
 * <p/>
 * See the javadoc of these annotations for more info on how you can use them.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModule implements Module {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SpringModule.class);

    /* All created Spring application contexts per class */
    private Map<Class<?>, ApplicationContext> applicationContexts = new HashMap<Class<?>, ApplicationContext>();


    /**
     * Initializes this module using the given <code>Configuration</code> object
     *
     * @param configuration The configuration, not null
     */
    public void init(Configuration configuration) {
    }


    /**
     * Gets the spring bean with the given name. The given test instance, by using {@link SpringApplicationContext},
     * determines the application context in which to look for the bean.
     * <p/>
     * A UnitilsException is thrown when the no bean could be found for the given name.
     *
     * @param testObject The test instance, not null
     * @param name       The name, not null
     * @return The bean, not null
     */
    public Object getSpringBean(Object testObject, String name) {
        try {
            return getApplicationContext(testObject).getBean(name);

        } catch (BeansException e) {
            throw new UnitilsException("Unable to get Spring bean. No Spring bean found for name " + name);
        }
    }


    /**
     * Gets the spring bean with the given type. The given test instance, by using {@link SpringApplicationContext},
     * determines the application context in which to look for the bean.
     * If more there is not exactly 1 possible bean assignment, an UnitilsException will be thrown.
     *
     * @param testObject The test instance, not null
     * @param type       The type, not null
     * @return The bean, not null
     */
    public Object getSpringBeanByType(Object testObject, Class<?> type) {
        Map beans = getApplicationContext(testObject).getBeansOfType(type);
        if (beans == null || beans.size() == 0) {
            throw new UnitilsException("Unable to get Spring bean by type. No Spring bean found for type " + type.getSimpleName());
        }
        if (beans.size() > 1) {
            throw new UnitilsException("Unable to get Spring bean by type. More than one possible Spring bean for type " + type.getSimpleName() + ". Possible beans; " + beans);
        }
        return beans.values().iterator().next();
    }


    /**
     * Gets the application context for this test. A new one will be created if it does not exist yet. If a superclass
     * has also declared the creation of an application context, this one will be retrieved (or created if it was not
     * created yet) and used as parent context for this classes context.
     * <p/>
     * If needed, an application context will be created using the settings of the {@link SpringApplicationContext}
     * annotation.
     * <p/>
     * If a class level {@link SpringApplicationContext} annotation is found, the passed locations will be loaded using
     * a <code>ClassPathXmlApplicationContext</code>.
     * Custom creation methods can be created by annotating them with {@link SpringApplicationContext}. They
     * should have an <code>ApplicationContext</code> as return type and either no or exactly 1 argument of type
     * <code>ApplicationContext</code>. In the latter case, the current configured application context is passed as the argument.
     * <p/>
     * A UnitilsException will be thrown if no context could be retrieved or created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    @SuppressWarnings({"unchecked"})
    public ApplicationContext getApplicationContext(Object testObject) {
        // get or create context
        ApplicationContext applicationContext = getApplicationContext(testObject, testObject.getClass());

        // check no application context was created
        if (applicationContext == null) {
            throw new UnitilsException("No application context was created. Make sure the context can be created by annotating the class, a field or a method " +
                    "with a @" + SpringApplicationContext.class.getSimpleName() + " annotation.");
        }
        return applicationContext;
    }


    /**
     * Gets the application context for this class and sets it on the fields and setter methods that are
     * annotated with {@link SpringApplicationContext}. If no application context could be created, an
     * UnitilsException will be raised.
     *
     * @param testObject The test instance, not null
     */
    public void injectApplicationContext(Object testObject) {
        // inject into fields annotated with @SpringApplicationContext
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringApplicationContext.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getApplicationContext(testObject));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the application context to field annotated with @" + SpringApplicationContext.class.getSimpleName(), e);
            }
        }

        // inject into methods annotated with @SpringApplicationContext
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), SpringApplicationContext.class, false);
        for (Method method : methods) {
            if (!(isSetter(method))) {
                continue;
            }
            try {
                invokeMethod(testObject, method, getApplicationContext(testObject));

            } catch (Exception e) {
                throw new UnitilsException("Unable to assign the application context to setter annotated with @" + SpringApplicationContext.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBean}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeans(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBean.class);
        for (Field field : fields) {
            try {
                SpringBean springBeanAnnotation = field.getAnnotation(SpringBean.class);
                setFieldValue(testObject, field, getSpringBean(testObject, springBeanAnnotation.value()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBean.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByType}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByType(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBeanByType(testObject, field.getType()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByName}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByName(Object testObject) {

        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets or creates the application context for the given class in the class hierarchy of the given test object.
     * This will first (recursively) get the context for the super class of the test class. This context will
     * then be passed as the parentApplicationContext for the application contexts created for this class.
     * <p/>
     * An application context for a certain class type, will only be created once. The next time a context is requested
     * for this class type, the same context will be returned.
     * <p/>
     * If a context needs to be created, first the class level @SpringApplicationContext, if there is one, is used for
     * creating a context. Next the field level {@link SpringApplicationContext} annotatations are used to create a context.
     * Finally the custom create methods (annotated with @SpringApplicationContext) are used for creating a context.
     * <p/>
     * Each time a new context is created, the current context value is set as the parent of the new context. Eg, if
     * a superclass creates a context and subclass also, the superclass context is set the parent of the subclass
     * context. Another example, if a class level annotation creates a context and a field annotation creates a context
     * the class level context is set as the parent for the field level context.
     * <p/>
     * If no locations are specified in field and class level annotations, no new context is created. For class level
     * annotations a warning will be logged.
     *
     * @param testObject The test instance, not null
     * @param testClass  The current class in the hierarchy, can be null
     * @return the new application context, null if none was found or created
     */
    protected ApplicationContext getApplicationContext(Object testObject, Class<?> testClass) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // check whether it already exists
        ApplicationContext applicationContext = applicationContexts.get(testClass);
        if (applicationContext != null) {
            return applicationContext;
        }

        // find parent application context recursively
        applicationContext = getApplicationContext(testObject, testClass.getSuperclass());

        // create application context for class level @SpringApplicationContext
        applicationContext = createApplicationContextForClass(testClass, applicationContext);

        // create application context for field level @SpringApplicationContext
        applicationContext = createApplicationContextForFields(testClass, applicationContext);

        // call methods annotated with @SpringApplicationContext passing current application context if requested
        applicationContext = createApplicationContextForMethods(testObject, testClass, applicationContext);

        // store application context
        if (applicationContext != null) {
            applicationContexts.put(testClass, applicationContext);
        }
        return applicationContext;
    }


    /**
     * Creates an application context by looking at the declared {@link SpringApplicationContext} for the given class.
     * If no such annotation is found for the given class, the parentApplicationContext is returned.
     * If it is found, the passed locations are used to create an <code>ApplicationContext</code> and
     * the parentApplicationContext is set as parent.
     * <p/>
     * If an annotation was found, but no locations were passed, the parentApplicationContext is returned and
     * a warning is logged.
     *
     * @param testClass                The current class in the hierarchy, not null
     * @param parentApplicationContext The current application context
     * @return the new application context or the parentApplicationContext (can be null)
     */
    protected ApplicationContext createApplicationContextForClass(Class<?> testClass, ApplicationContext parentApplicationContext) {
        // get class annotation
        SpringApplicationContext springApplicationContextAnnotation = testClass.getAnnotation(SpringApplicationContext.class);
        if (springApplicationContextAnnotation == null) {
            return parentApplicationContext;
        }

        String[] locations = springApplicationContextAnnotation.value();
        if (locations == null || locations.length == 0 || (locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
            logger.warn("No locations found for class level @" + SpringApplicationContext.class.getSimpleName() + " annotation. Ignoring annotation, no application context is created.");
            return parentApplicationContext;
        }
        return createApplicationContext(locations, parentApplicationContext);
    }


    /**
     * Creates an application context for all fields having a {@link SpringApplicationContext} annotation. If no such
     * annotation is found for the given class, the parentApplicationContext is returned. If it is found and has locations
     * specified, the locations are used to create a new <code>ApplicationContext</code> with the parentApplicationContext
     * set as parent.
     * <p/>
     * If more than 1 field is found, a new context will created for all these fields each time using the context created
     * for the previous field as parent for the new context.
     * <p/>
     * This will not assign the application context to the field. The assignation is done by {@link #injectApplicationContext(Object)}
     * and only injects the last created application context. There can only be 1 application context per class, so if you have
     * multiple annotated fields, they will all be assigned the same context instance.
     *
     * @param testClass                The current class in the hierarchy, not null
     * @param parentApplicationContext The current application context
     * @return the new application context or the parentApplicationContext (can be null)
     */
    protected ApplicationContext createApplicationContextForFields(Class<?> testClass, ApplicationContext parentApplicationContext) {
        // start with parent context as current application context
        ApplicationContext applicationContext = parentApplicationContext;

        // get annotated field and create contexts when they have locations
        List<Field> fields = getFieldsAnnotatedWith(testClass, SpringApplicationContext.class);
        for (Field field : fields) {
            try {
                SpringApplicationContext springApplicationContextAnnotation = field.getAnnotation(SpringApplicationContext.class);
                applicationContext = createApplicationContext(springApplicationContextAnnotation.value(), applicationContext);

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            }
        }
        return applicationContext;
    }


    /**
     * Creates an application context by calling all methods declared with @SpringApplicationContext.
     * These methods should have one of following exact signatures:
     * <ul>
     * <li>ApplicationContext createMethodName() or</li>
     * <li>ApplicationContext createMethodName(ApplicationContext applicationContext)</li>
     * </ul>
     * The second version receives the current created application context, for example one that was created by a class or
     * superclasses @SpringApplicationContext).
     * They both should return an application context, either a new one (possibly setting the passed application
     * context as parent) or the given parent context (after for example changing some values).
     * <p/>
     * This method will only look at the creation methods declared in the given testClass, superclasses are not looked at.
     * <p/>
     * If there are also locations specified in the {@link SpringApplicationContext} annotation, it will first create
     * a new application context for these locations (using the current application context as parent) and then
     * pass this context as argument to the annotated method.
     *
     * @param testObject               The test instance, not null
     * @param testClass                The current class in the hierarchy, not null
     * @param parentApplicationContext The current application context
     * @return the new application context or the parentApplicationContext (can be null)
     */
    protected ApplicationContext createApplicationContextForMethods(Object testObject, Class<?> testClass, ApplicationContext parentApplicationContext) {
        // start with parent context as current application context
        ApplicationContext applicationContext = parentApplicationContext;

        // call all @SpringApplicationContext methods passing current application context if requested
        List<Method> methods = getMethodsAnnotatedWith(testClass, SpringApplicationContext.class, false);
        for (Method method : methods) {

            // create contexts if a location is specified
            SpringApplicationContext springApplicationContextAnnotation = method.getAnnotation(SpringApplicationContext.class);
            applicationContext = createApplicationContext(springApplicationContextAnnotation.value(), applicationContext);

            // do not invoke setter methods
            if (isSetter(method)) {
                continue;
            }
            Class<?>[] argumentTypes = method.getParameterTypes();
            if (argumentTypes.length > 1 || (argumentTypes.length == 1 && argumentTypes[0] != ApplicationContext.class) || method.getReturnType() != ApplicationContext.class) {
                throw new UnitilsException("Unable to invoke method annotated with @" + SpringApplicationContext.class.getSimpleName() +
                        ". Ensure that this method has following signature: ApplicationContext myMethod(" + ApplicationContext.class.getName() + " context) or ApplicationContext myMethod()");
            }
            try {
                // call method
                if (argumentTypes.length == 0) {
                    applicationContext = invokeMethod(testObject, method);
                } else {
                    applicationContext = invokeMethod(testObject, method, applicationContext);
                }
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + SpringApplicationContext.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
        return applicationContext;
    }


    /**
     * Creates an application context for the given locations. A <code>ClassPathXmlApplicationContext</code> will be
     * created with the given application context as parent.
     * <p/>
     * If the locations array is null or empty or contains a single empty location, no new context is created. The parent
     * application context will be returned instead.
     *
     * @param locations                The file locations
     * @param parentApplicationContext The parent context
     * @return The new context or the parent if no new context was created
     */
    protected ApplicationContext createApplicationContext(String[] locations, ApplicationContext parentApplicationContext) {
        try {
            if (locations == null || locations.length == 0 || (locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
                // no or empty location specified, return parent
                return parentApplicationContext;
            }

            // create application context
            return new ClassPathXmlApplicationContext(locations, parentApplicationContext);

        } catch (Throwable t) {
            throw new UnitilsException("Unable to create application context for locations " + Arrays.toString(locations));
        }
    }


    /**
     * @return The {@link TestListener} for this module
     */
    public TestListener createTestListener() {
        return new SpringTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    private class SpringTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectApplicationContext(testObject);
            assignSpringBeans(testObject);
            assignSpringBeansByType(testObject);
            assignSpringBeansByName(testObject);
        }


    }

}
