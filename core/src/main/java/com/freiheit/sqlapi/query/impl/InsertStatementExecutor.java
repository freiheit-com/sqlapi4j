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

import com.freiheit.sqlapi.generate.SqlDialect;
import com.freiheit.sqlapi.generate.SqlGenerator;
import com.freiheit.sqlapi.generate.SqlQueryType;
import com.freiheit.sqlapi.generate.impl.PreparedStatementData;
import com.freiheit.sqlapi.query.statements.InsertStatement;

@ParametersAreNonnullByDefault
public class InsertStatementExecutor extends AbstractInsertOrUpdateStatementExecutor<InsertStatement> {

	public InsertStatementExecutor(final SqlQueryType queryType, final SqlDialect sqlDdialect, final SqlGenerator sqlGenerator) {
	    super(queryType, sqlDdialect, sqlGenerator);
	}

	@Override
    protected String generateStatement(final PreparedStatementData preparedStatementData, final InsertStatement statement) {
        return getGenerator().generateInsertStatement(
                getDialect(), statement.getTable(), statement.getRows(), statement.getFromDefs(), statement.getWhere(), preparedStatementData
        );
    }

}
