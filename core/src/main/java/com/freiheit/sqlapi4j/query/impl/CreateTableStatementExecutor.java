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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Nullable;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.query.executors.SqlStatementExecutor;
import com.freiheit.sqlapi4j.query.statements.CreateTableStatement;

public class CreateTableStatementExecutor implements SqlStatementExecutor<CreateTableStatement, Void> {

	private final SqlDialect _dialect;
	private final SqlGenerator _generator;

	public CreateTableStatementExecutor( final SqlDialect dialect, final SqlGenerator generator) {
		_dialect= dialect;
		_generator= generator;
	}

	@Override
	@Nullable
	public Void execute(final Connection conn, final CreateTableStatement statement) throws SQLException {
		final String sqlStr= _generator.generateCreateTableStatement( _dialect, statement.getTable());
		final Statement stmt= conn.createStatement();
		stmt.execute( sqlStr);
		return null;
	}

	@Override
	public String toString(final CreateTableStatement statement) {
		return _generator.generateCreateTableStatement( _dialect, statement.getTable());
	}

}
