package org.unitils.dbunit.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Multiple {@link DataSet}.
 * 
 * @author wiw
 * @since 3.4.1
 */
@Target({
    TYPE, METHOD
})
@Retention(RUNTIME)
@Inherited
public @interface DataSets {
    /**
     * One or more {@link DataSet}
     * 
     * @return {@link java.lang.reflect.Array}
     */
    DataSet[] value();
}
