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
package com.freiheit.sqlapi4j.query.impl;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.statements.InsertStatement;

public final class InsertStatementImpl implements InsertStatement {
    @Nonnull private final TableDef _table;
    @Nonnull private final List<ColumnAssignment<?>[]> _values;
    @CheckForNull private final BooleanExpression _whereCondition;
    @CheckForNull private final FromDef[] _fromDefs;

    public InsertStatementImpl(
        @Nonnull final TableDef table,
        @Nullable final BooleanExpression whereCondition,
        @Nullable final FromDef[] fromDefs,
        @Nonnull final List<ColumnAssignment<?>[]> values
    ) {
        super();
        _table = table;
        _whereCondition = whereCondition;
        _fromDefs = fromDefs;
        _values = values;
    }

    @Override
    public FromDef[] getFromDefs() {
        return _fromDefs;
    }


    @Override
    public List<ColumnAssignment<?>[]> getRows() {
        return _values;
    }


    @Override
    public TableDef getTable() {
        return _table;
    }

    @Override
    public BooleanExpression getWhere() {
        return _whereCondition;
    }
}
