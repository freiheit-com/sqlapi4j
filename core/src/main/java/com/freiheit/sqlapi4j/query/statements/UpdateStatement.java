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
package com.freiheit.sqlapi4j.query.statements;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.Sql.BooleanCombination;

/**
 * Data of an update statement.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public interface UpdateStatement {

    @Nonnull
    TableDef getTable();

    @CheckForNull
    ColumnAssignment<?>[] getColumnAssignments();

    @CheckForNull
    BooleanCombination getCondition();
}
