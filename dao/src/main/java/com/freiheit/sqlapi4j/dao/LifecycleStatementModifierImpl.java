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
package com.freiheit.sqlapi4j.dao;

import static com.freiheit.sqlapi4j.dao.StatementModifierUtil.appendTimestamp;

import java.util.ArrayList;
import java.util.List;

import com.freiheit.sqlapi4j.dao.meta.CreatedAtColumn;
import com.freiheit.sqlapi4j.dao.meta.UpdatedAtColumn;
import com.freiheit.sqlapi4j.domain.type.Clock;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.impl.InsertStatementImpl;
import com.freiheit.sqlapi4j.query.impl.UpdateStatementImpl;
import com.freiheit.sqlapi4j.query.statements.InsertStatement;
import com.freiheit.sqlapi4j.query.statements.UpdateStatement;

/**
 * Implementation of {@link StatementEnhancer} that
 * adds creation and modification information to insert and update statements.
 *
 * All values are only inserted if the table has a corresponding column and the statement does not have an
 * explicit value set for that column.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public class LifecycleStatementModifierImpl implements StatementEnhancer {
    private final Clock<?> _clock;

    public LifecycleStatementModifierImpl(final Clock<?> clock) {
        _clock = clock;
    }

    @Override
    public InsertStatement enhance(final InsertStatement stmt) {
        final TableDef table = stmt.getTable();
        final List<ColumnAssignment<?>[]> initialRows = stmt.getRows();
        final List<ColumnAssignment<?>[]> rows = new ArrayList<ColumnAssignment<?>[]>(initialRows.size());
        for (final ColumnAssignment<?>[] row : initialRows) {
            rows.add(addDateColumns(table, row));
        }
        return new InsertStatementImpl(table, stmt.getWhere(), stmt.getFromDefs(), rows);
    }

    @SuppressWarnings( "unchecked" )
    private ColumnAssignment<?>[] addDateColumns(final TableDef table, final ColumnAssignment<?>[] columnAssignments) {

        ColumnAssignment<?>[] columns = new ColumnAssignment<?>[columnAssignments.length];
        System.arraycopy(columnAssignments, 0, columns, 0, columns.length);

        columns = appendTimestamp(_clock, CreatedAtColumn.class, table, columns);
        columns = appendTimestamp(_clock, UpdatedAtColumn.class, table, columns);

        return columns;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public UpdateStatement enhance(final UpdateStatement stmt) {
        final ColumnAssignment<?>[] values = stmt.getColumnAssignments();
        final TableDef table = stmt.getTable();
        return new UpdateStatementImpl(appendTimestamp(_clock, UpdatedAtColumn.class, table, values), stmt.getCondition(), table);
    }
}
