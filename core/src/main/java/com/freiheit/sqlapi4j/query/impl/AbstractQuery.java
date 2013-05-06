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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.SqlCommand;
import com.freiheit.sqlapi4j.query.Sql.And;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;

@ParametersAreNonnullByDefault
abstract class AbstractQuery implements SqlCommand<SelectStatement> {

	// private String _preparedStatement= null;

    @Nonnull private final SqlAst _syntax = new SqlAst();

	protected AbstractQuery(
	        final SelectListItem<?>... items
	) {
	    _syntax.setSelectItems(items);
    }

    @Nonnull
    protected SqlAst getSyntax() {
        return _syntax;
    }

    @Override
    public SelectStatement stmt() {
        return _syntax;
    }

	@Override
	public String toString() {
	    return stmt().toString();
	}

	protected void applyFrom(final FromDef... fromDef) {
	    getSyntax().setFromDef(fromDef);
	}

    protected void applyWhere(final BooleanExpression... booleanExpressions) {
        getSyntax().setWhere(booleanExpressions.length == 0 ? null : new And(booleanExpressions));
    }

    protected void applyGroupBy(final SelectListItem<?>... items) {
        getSyntax().setGroupItems(items);
    }

    protected void applyHaving(final BooleanExpression... conditions) {
        getSyntax().setHaving(conditions.length == 0 ? null : new And(conditions));
    }

    protected void applyOrderBy(final SelectListItem<?>... items) {
        getSyntax().setOrderItems(items);
    }

    protected void applyLimit(@Nonnegative final int limit) {
        getSyntax().setLimitNum(Integer.valueOf(limit));
    }

    protected void applyOffset(@Nonnegative final int offset) {
        getSyntax().setOffsetNum(Integer.valueOf(offset));
    }

}
