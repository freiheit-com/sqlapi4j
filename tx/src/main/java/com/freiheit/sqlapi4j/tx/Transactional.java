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

}
