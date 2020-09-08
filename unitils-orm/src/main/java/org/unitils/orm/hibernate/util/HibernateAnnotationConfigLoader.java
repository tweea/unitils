package org.unitils.orm.hibernate.util;

import java.lang.reflect.Method;

import org.hibernate.cfg.Configuration;
import org.unitils.orm.common.util.OrmConfig;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.util.AnnotationConfigLoader;
import org.unitils.util.CollectionUtils;

public class HibernateAnnotationConfigLoader
    extends AnnotationConfigLoader<HibernateSessionFactory, OrmConfig> {
    public HibernateAnnotationConfigLoader() {
        super(HibernateSessionFactory.class);
    }

    @Override
    protected boolean isConfiguringAnnotation(HibernateSessionFactory annotation) {
        return annotation.value().length > 0;
    }

    @Override
    protected OrmConfig createResourceConfig(HibernateSessionFactory configuringAnnotation, Method customConfigMethod) {
        return new OrmConfig(CollectionUtils.asSet(configuringAnnotation.value()), customConfigMethod);
    }

    @Override
    protected boolean isCustomConfigMethod(Method annotatedMethod) {
        return annotatedMethod.getReturnType().toString().equals("void") && annotatedMethod.getParameterTypes().length == 1
            && Configuration.class.isAssignableFrom(annotatedMethod.getParameterTypes()[0]);
    }
}
