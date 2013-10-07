/**
 * Copyright 2013 freiheit.com technologies gmbh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.freiheit.sqlapi4j.tx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotates all methods that should be executed within an annotation.
 *
 * <p><b>IMPORTANT:</b> A transaction will only be automatically created if the annotated
 * method is part of an instance created by guice. If it is part of a callback instance
 * which was created with "new" and not with guice, the transactional annotation
 * will not have an effect! You will need to use the TransactionTemplate in such cases.</p>
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 * @author Christoph Mewes (christohp.mewes@freiheit.com)
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {


    /**
     * Set the isolationlevel of this transaction.
     * Can be one of
     * <ul>
     * <li>{@link IsolationLevel#READ_COMMITTED}</li>
     * <li>{@link IsolationLevel#READ_UNCOMMITTED}</li>
     * <li>{@link IsolationLevel#REPEATABLE_READ}</li>
     * <li>{@link IsolationLevel#SERIALIZABLE}</li>
     * </ul>
     */
    IsolationLevel isolation() default IsolationLevel.READ_COMMITTED;

    /**
     * <b>ATTENTION:</b> This setting will lead to a deadlock, if used within a transaction
     * that touches one of the tables your new transaction wants to touch. Foreign-Key references
     * are problematic as well.
     *
     * @return
     */
    boolean propagateNew() default false;
}
