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

import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.FromDef;
import com.freiheit.sqlapi.query.SelectListItem;
import com.freiheit.sqlapi.query.SubQuery;
import com.freiheit.sqlapi.query.clause.SubFromClause;
import com.freiheit.sqlapi.query.clause.SubGroupByClause;
import com.freiheit.sqlapi.query.clause.SubHavingClause;
import com.freiheit.sqlapi.query.clause.SubLimitClause;
import com.freiheit.sqlapi.query.clause.SubOffsetClause;
import com.freiheit.sqlapi.query.clause.SubOrderByClause;
import com.freiheit.sqlapi.query.clause.SubWhereClause;

@ParametersAreNonnullByDefault
public class SubQueryImpl<T> extends AbstractQuery implements SubFromClause<T>, SubWhereClause<T> {

	public SubQueryImpl(
	    final SelectListItem<?>... items
	) {
	    super(items);
    }

	@Override
	public SubWhereClause<T> from(final FromDef... fromDefs) {
	    applyFrom(fromDefs);
	    return this;
	}

	@Override
	public SubGroupByClause<T> where(final BooleanExpression... booleanExpressions) {
	    applyWhere(booleanExpressions);
	    return this;
	}

	@Override
	public SubHavingClause<T> groupBy(final SelectListItem<?>... items) {
	    applyGroupBy(items);
	    return this;
	}

	@Override
	public SubOrderByClause<T> having(final BooleanExpression... conditions) {
	    applyHaving(conditions);
	    return this;
	}

	@Override
    public SubLimitClause<T> orderBy(final SelectListItem<T> item) {
	    applyOrderBy(item);
	    return this;
	}

	@Override
	public SubOffsetClause<T> limit(final int limit) {
	    applyLimit(limit);
	    return this;
	}

	@Override
	public SubQuery<T> offset(final int offset) {
	    applyOffset(offset);
	    return this;
	}

}
