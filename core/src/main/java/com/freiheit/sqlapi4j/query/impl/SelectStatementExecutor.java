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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.generate.SqlQueryType;
import com.freiheit.sqlapi4j.generate.impl.PreparedStatementData;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;

@ParametersAreNonnullByDefault
class SelectStatementExecutor extends AbstractQueryStatementExecutor<SelectStatement, SelectResult>{

	protected SelectStatementExecutor(
	    final SqlQueryType sqlQueryType,
	    final SqlDialect dialect,
	    final SqlGenerator generator
	) {
	    super(sqlQueryType, dialect, generator);
    }

    @Override
	public SelectResult execute(final Connection conn, final SelectStatement statement) throws SQLException {
		if( getSqlQueryType() == SqlQueryType.PREPARED) {
			return executePrepared( conn, statement);
		}
		return executePlainSql( conn, statement );
	}

    @Nonnull
	private SelectResult executePlainSql(final Connection conn, final SelectStatement statement) throws SQLException {
		// FIXME (CL): https://github.com/greenhornet/freiheit_sqlapi/issues/11
               // unsafe default... how can we use this safely?
		final Statement stmt= conn.createStatement();
		final ResultFlags resFlags= new ResultFlags();
		final String sql= getGenerator().generateQueryString( getDialect(), statement, resFlags, PreparedStatementData.NO_PREPARED_STATEMENT);
		final ResultSet rs= stmt.executeQuery( sql);
		return new SqlResultImpl( getDialect(), statement.getSelectItems(), rs, stmt);
	}

    @Nonnull
	private SelectResult executePrepared(final Connection conn, final SelectStatement statement) throws SQLException {
		final ResultFlags resFlags= new ResultFlags();
		final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
		final String preparedStatement= getGenerator().generateQueryString( getDialect(), statement, resFlags, preparedStatementData);
		//System.out.println( preparedStatement);

		final PreparedStatement pstmt= conn.prepareStatement( preparedStatement);
		final Iterator<?> it= preparedStatementData.getValues();
		int i= 1;
		while( it.hasNext()) {
			pstmt.setObject( i, it.next());
			++i;
		}
		final ResultSet rs= pstmt.executeQuery();

		return new SqlResultImpl( getDialect(), statement.getSelectItems(), rs, pstmt);
	}

	@Override
    public String toString(final SelectStatement statement) {
		//return "values= " + _sqlGenerator.extractPreparedStatementParams( _dialect, _syntax) + "\nsql: " + _sqlGenerator.generateQueryString( _dialect, _syntax, new ResultFlags());
		switch( getSqlQueryType()) {
		case PREPARED:
			final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
			return "sql: " + getGenerator().generateQueryString( getDialect(), statement, new ResultFlags(), preparedStatementData) + " ; values: " + preparedStatementData;
		default:
			return "sql: " + getGenerator().generateQueryString( getDialect(), statement, new ResultFlags(), PreparedStatementData.NO_PREPARED_STATEMENT);
		}
	}

}
