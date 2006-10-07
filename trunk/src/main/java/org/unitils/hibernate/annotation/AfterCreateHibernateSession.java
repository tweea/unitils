package org.unitils.hibernate.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation indicating that this method should be executed after a <code>org.hibernate.Session</code> has been created.
 * The annotated method should have following signature: void myMethod(org.hibernate.Session session)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterCreateHibernateSession {
}
