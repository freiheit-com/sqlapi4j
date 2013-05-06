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
package com.freiheit.sqlapi.query.clause;

/**
 * Locking mode of a select-query.
 * 
 * @author Fabian Loewner (fabian.loewner@freiheit.com)
 *
 */
public enum LockMode {
    /**
     * Obtains row-write-locks. This translates to an "select for update"-statement.
     */
    UPGRADE,
    /**
     * Obtains row-write-locks, but does not wait for locks. (Oracle-specific).
     * This translates to a "select for update nowait"-statement 
     */
    UPGRADE_NO_WAIT,
    /**
     * Obtains row-write-locks, but skips rows, which already have write locks from
     * someone else (Oracle-specific). This translates to a "select for update skip locked"-statement.
     */
    UPGRADE_SKIP_LOCKED,
    
    ;
}
