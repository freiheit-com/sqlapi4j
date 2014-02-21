/**
 * Copyright 2014 freiheit.com technologies gmbh
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
package com.freiheit.sqlapi4j.meta;

import com.freiheit.sqlapi4j.query.FromDef;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

/**
 * A table-alias.
 *
 * @author Philip Schwartau (philip.schwartau@freiheit.com)
 */
public class TableAlias implements FromDef {

    @Nonnull private final TableDef _tableDef;
    @Nonnull private final String _alias;
    @Nonnull private final ConcurrentHashMap<String, AbstractColumnDef<?>> _columnDefCache;

    TableAlias(@Nonnull final TableDef tableDef, @Nonnull final String alias) {
        _tableDef = tableDef;
        _alias = alias;

        _columnDefCache = new ConcurrentHashMap<String, AbstractColumnDef<?>>();
    }

    @Nonnull
    public String getTableName() {
        return _tableDef.getTableName() + " " + _alias;
    }

    @Nonnull
    public <T> ColumnDef<T> forColumn(@Nonnull final ColumnDef<T> column) {
        if (!_columnDefCache.contains(column.name())) {
            final ColumnDef<T> aliasCol = new ColumnDef<T>(column.name(), column.type()) {
                @Override
                void setTable(final TableDef table) {
                    super.setTable(table);
                    setFqName(_alias + "." + name());
                }
            };
            _columnDefCache.putIfAbsent(column.name(), setTableAndReturn(column, aliasCol));
        }
        return (ColumnDef<T>)_columnDefCache.get(column.name());
    }

    @Nonnull
    public <T> ColumnDefNullable<T> forColumn(@Nonnull final ColumnDefNullable<T> column) {
        if (!_columnDefCache.contains(column.name())) {
            final ColumnDefNullable<T> aliasCol = new ColumnDefNullable<T>(column.name(), column.type()) {
                @Override
                void setTable(final TableDef table) {
                    super.setTable(table);
                    setFqName(_alias + "." + name());
                }
            };
            _columnDefCache.putIfAbsent(column.name(), setTableAndReturn(column, aliasCol));
        }
        return (ColumnDefNullable<T>)_columnDefCache.get(column.name());
    }

    private <T, C extends AbstractColumnDef<T>> C setTableAndReturn(final C column, final C aliasCol) {
        if (!_tableDef.equals(column.getTable())) {
            throw new IllegalArgumentException(
                    "AliasTable.forColumn called with a column that is not attached to the table aliased."
            );
        }
        aliasCol.setTable(_tableDef);
        return aliasCol;
    }

}
