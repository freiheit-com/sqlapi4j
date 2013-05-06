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

import com.freiheit.sqlapi4j.meta.SequenceDef;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.CreateTableCommand;
import com.freiheit.sqlapi4j.query.DeleteCommand;
import com.freiheit.sqlapi4j.query.InsertCommand;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.SelectSequenceCommand;
import com.freiheit.sqlapi4j.query.SqlBuilder;
import com.freiheit.sqlapi4j.query.UpdateCommand;
import com.freiheit.sqlapi4j.query.clause.FromClause;
import com.freiheit.sqlapi4j.query.clause.SubFromClause;

public class SqlBuilderImpl implements SqlBuilder {


	/**
	 * Standard constructor to create a QueryBuilder.
	 */
	public SqlBuilderImpl() {
	}

	@Override
	public InsertCommand insert( final TableDef table) {
		return new InsertCommandImpl(table);
	}

	@Override
	public UpdateCommand update( final TableDef table) {
		return new UpdateCommandImpl(table);
	}

	@Override
	public DeleteCommand delete( final TableDef table) {
		return new DeleteCommandImpl(table);
	}

	@Override
	public FromClause select( final SelectListItem<?>... items) {
		return new SelectCommandImpl(items);
	}

	@Override
	public <T> SubFromClause<T> subSelect( final SelectListItem<T> item) {
		return new SubQueryImpl<T>(item);
	}

	@Override
	public CreateTableCommand createTable( final TableDef table) {
		return new CreateTableCommandImpl(table);
	}

	@Override
	public SelectSequenceCommand nextval( final SequenceDef sequence) {
		return new SequenceImpl( sequence);
	}
}
