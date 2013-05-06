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
package com.freiheit.sqlapi.query.impl;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.ColumnAssignment;
import com.freiheit.sqlapi.query.FromDef;
import com.freiheit.sqlapi.query.statements.InsertStatement;

public final class InsertStatementImpl implements InsertStatement {
    @Nonnull private final TableDef _table;
    @Nonnull private final List<ColumnAssignment<?>[]> _values;
    @CheckForNull private final BooleanExpression _whereCondition;
    @CheckForNull private final FromDef[] _fromDefs;

    public InsertStatementImpl(
        @Nonnull final TableDef table,
        @Nonnull final BooleanExpression whereCondition,
        @CheckForNull final FromDef[] fromDefs,
        @CheckForNull final List<ColumnAssignment<?>[]> values
    ) {
        super();
        _table = table;
        _whereCondition = whereCondition;
        _fromDefs = fromDefs;
        _values = values;
    }

    @Override
    @CheckForNull
    public FromDef[] getFromDefs() {
        return _fromDefs;
    }


    @Override
    @Nonnull
    public List<ColumnAssignment<?>[]> getRows() {
        return _values;
    }


    @Override
    @Nonnull
    public TableDef getTable() {
        return _table;
    }

    @Override
    @CheckForNull
    public BooleanExpression getWhere() {
        return _whereCondition;
    }
}
