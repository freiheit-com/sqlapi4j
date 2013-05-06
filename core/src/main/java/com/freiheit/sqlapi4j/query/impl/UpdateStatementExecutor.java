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
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.generate.SqlQueryType;
import com.freiheit.sqlapi4j.generate.impl.PreparedStatementData;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.Sql.BooleanCombination;
import com.freiheit.sqlapi4j.query.statements.UpdateStatement;


@ParametersAreNonnullByDefault
class UpdateStatementExecutor extends AbstractInsertOrUpdateStatementExecutor<UpdateStatement> {

	@CheckForNull private ColumnAssignment<?>[] _values;
	@CheckForNull private BooleanCombination _booleanCombination;

	public UpdateStatementExecutor(final SqlQueryType queryType, final SqlDialect sqlDdialect, final SqlGenerator sqlGenerator) {
	    super(queryType, sqlDdialect, sqlGenerator);
	}

    @Override
    protected String generateStatement(final PreparedStatementData preparedStatementData, final UpdateStatement statement) {
        final ColumnAssignment<?>[] values = statement.getColumnAssignments();
        if (values == null) {
            throw new IllegalStateException("UPDATE without VALUES.");
        }
        return getGenerator().generateUpdateStatement( getDialect(), statement.getTable(), values, statement.getCondition(), preparedStatementData);
    }

}
