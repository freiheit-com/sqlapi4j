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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.InsertCommand;
import com.freiheit.sqlapi4j.query.InsertSelectQuery;
import com.freiheit.sqlapi4j.query.OrderItem;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.Sql.And;
import com.freiheit.sqlapi4j.query.clause.InsertFromClause;
import com.freiheit.sqlapi4j.query.clause.InsertGroupByClause;
import com.freiheit.sqlapi4j.query.clause.InsertHavingClause;
import com.freiheit.sqlapi4j.query.clause.InsertLimitClause;
import com.freiheit.sqlapi4j.query.clause.InsertOffsetClause;
import com.freiheit.sqlapi4j.query.clause.InsertOrderByClause;
import com.freiheit.sqlapi4j.query.clause.InsertWhereClause;
import com.freiheit.sqlapi4j.query.statements.InsertStatement;

@ParametersAreNonnullByDefault
public class InsertCommandImpl extends AbstractInsertOrUpdateCommand<InsertStatement> implements InsertCommand, InsertSelectQuery, InsertFromClause {

	@Nonnull private final List<ColumnAssignment<?>[]> _values= new ArrayList<ColumnAssignment<?>[]>();
	@CheckForNull private BooleanExpression _whereCondition;
	@CheckForNull private FromDef[] _fromDefs;

	public InsertCommandImpl(final TableDef table) {
	    super(table);
	}

	@Override
	public InsertCommand values(final ColumnValueAssignment<?>... values) {
		_values.add( values);
		return this;
	}

	@Override
	public InsertFromClause values(final ColumnAssignment<?>... values) {
		_values.add( values);
		return this;
	}

	@Override
	public InsertStatement stmt() {
	    return new InsertStatementImpl(getTable(), _whereCondition, _fromDefs, _values);
	}

	@Override
	public InsertGroupByClause where(final BooleanExpression... booleanExpressions) {
		_whereCondition= booleanExpressions.length == 0 ? null : new And( booleanExpressions);
		return this;
	}

	@Override
	public InsertWhereClause from(final FromDef... fromDef) {
		_fromDefs= fromDef;
		return this;
	}

	@Override
    public InsertHavingClause groupBy(final SelectListItem<?>... items) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public InsertOrderByClause having(final BooleanExpression... conditions) {
	    throw new UnsupportedOperationException();
	}

	@Override
    public InsertLimitClause orderBy(final OrderItem... items) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public InsertOffsetClause limit(@Nonnegative final int limit) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public InsertCommand offset(@Nonnegative final int offset) {
	    throw new UnsupportedOperationException();
	}

}
