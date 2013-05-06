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
package com.freiheit.sqlapi.dao;

import com.freiheit.sqlapi.query.statements.InsertStatement;
import com.freiheit.sqlapi.query.statements.UpdateStatement;

/**
 * Modifies SQL statements.
 *
 * Known uses of this interface include automatic assignments of timestamp column values.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public interface StatementEnhancer {

    /**
     * Enhance a statement - do not modify the instance but return a new copy instead.
     *
     * @param stmt
     * @return a copy of the original statement with or without changes, or the original statement if there are no changes.
     */
    InsertStatement enhance(InsertStatement stmt);

    /**
     * Enhance a statement - do not modify the instance but return a new copy instead.
     *
     * @param stmt
     * @return a copy of the original statement with or without changes, or the original statement if there are no changes.
     */
    UpdateStatement enhance(UpdateStatement stmt);
}
