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
package org.unitils.hibernate.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that the annotated test class tests methods using Hibernate to go to a database, so that the
 * {@link org.unitils.hibernate.HibernateModule} will provide it's services to it. Annotated classes are regarded as
 * database tests by the {@link org.unitils.db.DatabaseModule} and {@link org.unitils.dbunit.DbUnitModule}, so there is
 * no need for an extra {@link org.unitils.db.annotations.DatabaseTest} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HibernateTest {
}
