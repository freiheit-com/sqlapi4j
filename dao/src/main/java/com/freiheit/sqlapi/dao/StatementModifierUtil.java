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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.freiheit.sqlapi.dao.meta.ClockColumn;
import com.freiheit.sqlapi.domain.type.Clock;
import com.freiheit.sqlapi.meta.AbstractColumnDef;
import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.ColumnAssignment;

final class StatementModifierUtil {

    @Nonnull
    protected static ColumnAssignment<?>[] prependToArray(final ColumnAssignment<?> columnAssignment, final ColumnAssignment<?>... arg0) {
        final ColumnAssignment<?>[] result = new ColumnAssignment<?>[arg0.length + 1];
        result[0] = columnAssignment;
        System.arraycopy(arg0, 0, result, 1, arg0.length);
        return result;
    }

    @Nonnull
    protected static ColumnAssignment<?>[] concat(final ColumnAssignment<?>[] columnAssignments, final ColumnAssignment<?>... columnAssignments2) {
        final ColumnAssignment<?>[] result = new ColumnAssignment<?>[columnAssignments2.length + columnAssignments.length];
        System.arraycopy(columnAssignments, 0, result, 0, columnAssignments.length);
        System.arraycopy(columnAssignments2, 0, result,columnAssignments.length , columnAssignments2.length);
        return result;
    }

    @Nonnull
    protected static <C, T extends ClockColumn<C>> ColumnAssignment<?>[] appendTimestamp(
        final Clock<C> clock,
        final Class<T> cls,
        final TableDef table,
        final ColumnAssignment<?>... arg0)
    {
        final ColumnDef<C> c = findColumn(cls, table.getColumns());
        if (c != null && !contains(c, arg0) ) {
            final ColumnAssignment<C> columnAssignment = c.set(clock.getCurrentTime());
            return prependToArray(columnAssignment, arg0);
        }
        else {
            return arg0;
        }
    }

    protected static final boolean contains(final AbstractColumnDef<?> columnDef, final ColumnAssignment<?>... assignments) {
        for (final ColumnAssignment<?> a:assignments) {
            if (a.getLhsColumn().equals(columnDef)) {
                return true;
            }
        }
        return false;
    }

    @CheckForNull
    @SuppressWarnings("unchecked")
    private static <C, T extends AbstractColumnDef<C>> T findColumn(final Class<? extends ClockColumn<C>> class1, final AbstractColumnDef<?>[] columns) {
        for (final AbstractColumnDef<?> cd : columns) {
            if (class1.isInstance(cd)) {
                return (T) class1.cast(cd);
            }
        }
        return null;
    }

}
