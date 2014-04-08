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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.meta.AbstractColumnDef;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.BooleanExpressionVisitor;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.FromDefVisitor;

public class JoinDecl implements FromDef, OnPart {

    private final JoinType _joinType;
	private final TableDef _table1;
    private BooleanExpression _additionalExpr;
    private AbstractColumnDef<?> _column1;
	private final TableDef _table2;
	private AbstractColumnDef<?> _column2;

    private JoinDecl(@Nonnull final JoinType joinType,
	                 @Nonnull final TableDef table1,
	                 @Nullable final AbstractColumnDef<?> column1,
	                 @Nullable final TableDef table2,
	                 @Nullable final AbstractColumnDef<?> column2,
                     @Nullable final BooleanExpression additionalExpr) {
		_joinType= joinType;
		_table1= table1;
		_column1= column1;
		_table2= table2;
		_column2= column2;
        _additionalExpr = additionalExpr;
	}

    @Nonnull
    public static OnPart makeLeftOuterJoin(@Nonnull final TableDef left, @Nonnull final TableDef right) {
        if (left.equals(right)) {
            throw new IllegalArgumentException("self-joins are not yet supported and will result in runtime errors.");
        }
        return new JoinDecl(JoinType.LEFT_OUTER_JOIN, left, null, right, null, null);
    }

    @Override
    @Nonnull
    public FromDef on(final AbstractColumnDef<?> left, final AbstractColumnDef<?> right) {
        _column1 = left;
        _column2 = right;
        return this;
    }

    @Override
    @Nonnull
    public FromDef on(final AbstractColumnDef<?> left, final AbstractColumnDef<?> right, final BooleanExpression additionalExpr) {
        _column1 = left;
        _column2 = right;
        _additionalExpr = additionalExpr;
        return this;
    }

	@Nonnull
	public JoinType getJoinType() {
		return _joinType;
	}

	@Nonnull
    public TableDef getTable1() {
        return _table1;
    }

	@Nullable
    public AbstractColumnDef<?> getColumn1() {
        return _column1;
    }

	@Nullable
    public TableDef getTable2() {
        return _table2;
    }

	@Nullable
    public AbstractColumnDef<?> getColumn2() {
        return _column2;
    }

    @CheckForNull
    public BooleanExpression getAdditionalExpr() {
        return _additionalExpr;
    }

    @Nonnull
    public String getTableName() {
        throw new UnsupportedOperationException("getTableName() not supported for JoinDecl.");
    }

    public <T> T accept(@Nonnull final FromDefVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
