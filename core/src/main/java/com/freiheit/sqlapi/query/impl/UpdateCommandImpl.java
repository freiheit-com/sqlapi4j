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

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.ColumnAssignment;
import com.freiheit.sqlapi.query.Sql;
import com.freiheit.sqlapi.query.Sql.BooleanCombination;
import com.freiheit.sqlapi.query.UpdateCommand;
import com.freiheit.sqlapi.query.statements.UpdateStatement;


@ParametersAreNonnullByDefault
class UpdateCommandImpl extends AbstractInsertOrUpdateCommand<UpdateStatement> implements UpdateCommand {

	@CheckForNull private ColumnAssignment<?>[] _values;
	@CheckForNull private BooleanCombination _booleanCombination;

	public UpdateCommandImpl(final TableDef table) {
	    super(table);
	}

	@Override
	public UpdateCommand values(final ColumnAssignment<?>... values) {
		_values= values;
		return this;
	}

	@Override
	public UpdateCommand where(final BooleanExpression... booleanCombinations) {
		_booleanCombination= new Sql.And( booleanCombinations);
		return this;
	}

	@Override
	public UpdateStatement stmt() {
	    return new UpdateStatementImpl(_values, _booleanCombination, getTable());
	}

}
