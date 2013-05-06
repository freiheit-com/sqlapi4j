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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.FromDef;
import com.freiheit.sqlapi.query.SelectCommand;
import com.freiheit.sqlapi.query.SelectListItem;
import com.freiheit.sqlapi.query.clause.FromClause;
import com.freiheit.sqlapi.query.clause.GroupByClause;
import com.freiheit.sqlapi.query.clause.HavingClause;
import com.freiheit.sqlapi.query.clause.LimitClause;
import com.freiheit.sqlapi.query.clause.LockMode;
import com.freiheit.sqlapi.query.clause.LockingClause;
import com.freiheit.sqlapi.query.clause.OffsetClause;
import com.freiheit.sqlapi.query.clause.OrderByClause;
import com.freiheit.sqlapi.query.clause.WhereClause;

@ParametersAreNonnullByDefault
public class SelectCommandImpl extends AbstractQuery implements FromClause, WhereClause {

	// private String _preparedStatement= null;

	public SelectCommandImpl(
	    @Nonnull final SelectListItem<?>... items
	) {
	    super(items);
    }


	@Override
	public WhereClause from(final FromDef... fromDef) {
	    applyFrom(fromDef);
	    return this;
	}

	@Override
	public GroupByClause where(final BooleanExpression... booleanExpressions) {
	    applyWhere(booleanExpressions);
	    return this;
	}

    @Override
    public HavingClause groupBy(final SelectListItem<?>... items) {
        applyGroupBy(items);
        return this;
    }

    @Override
    public OrderByClause having(final BooleanExpression... conditions) {
        applyHaving(conditions);
        return this;
    }

    @Override
    public LimitClause orderBy(final SelectListItem<?>... items) {
        applyOrderBy(items);
        return this;
    }

    @Override
    public OffsetClause limit(final int limit) {
        applyLimit(limit);
        return this;
    }

    @Override
    public LockingClause offset(final int offset) {
        applyOffset(offset);
        return this;
    }

    @Override
    public SelectCommand withLockMode( final LockMode lockMode ) {
        applyLockMode(lockMode);
        return this;
    }

    private void applyLockMode( final LockMode lockMode ) {
        getSyntax().setLockMode(lockMode);
    }

    @Override
    public WhereClause useIndex(
            final String indexName) {
        getSyntax().setIndexName(indexName);
        return this;
    }

}
