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


import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.DeleteCommand;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.Sql.BooleanCombination;
import com.freiheit.sqlapi4j.query.statements.DeleteStatement;

@ParametersAreNonnullByDefault
class DeleteCommandImpl extends AbstractInsertOrUpdateCommand<DeleteStatement> implements DeleteCommand {

	@Nonnull private BooleanCombination _booleanCombination;

	public DeleteCommandImpl(final TableDef table) {
	    super(table);
	}

	@Override
	public DeleteStatement stmt() {
	    return new DeleteStatementImpl(_booleanCombination, getTable());
	}

	@Override
	public DeleteCommand where(final BooleanExpression... booleanCombinations) {
		_booleanCombination= new Sql.And( booleanCombinations);
		return this;
	}

}
