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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.meta.SequenceDef;
import com.freiheit.sqlapi4j.meta.DbType.DbLong;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.SqlResultRow;
import com.freiheit.sqlapi4j.query.statements.SelectSequenceStatement;

@ParametersAreNonnullByDefault
public class SelectSequenceStatementExecutor {

	@Nonnull private final SqlDialect _dialect;
	@Nonnull private final SqlGenerator _sqlGenerator;

	/**
	 * SelectListItem for the result value of executing the nextval statement, if no other
	 * item is provided.
	 *
	 * @author Klas Kalass (klas.kalass@freiheit.com)
	 */
	private static final class StandardSelectListItem implements SelectListItem<Long> {
	    private static final DbLong DB_LONG = new DbType.DbLong();

	    public static final StandardSelectListItem ITEM = new StandardSelectListItem();

	    @Override
	    @Nonnull
	    public String fqName() {
	        return "nextval";
	    }

	    @Override
	    public boolean isColumnName() {
	        return false;
	    }

	    @Override
	    @Nonnull
	    public String name() {
	        return "nextval";
	    }

        @Override
	    @Nonnull
	    public DbType<Long> type() {
	        return DB_LONG;
	    }
	}

	public SelectSequenceStatementExecutor(final SqlDialect dialect, final SqlGenerator sqlGenerator) {
		_dialect= dialect;
		_sqlGenerator= sqlGenerator;
	}

	//	@Override
	//	public long actVal( Connection conn) throws SQLException {
	//		return 0;
	//	}

	public long execute(final Connection conn, final SelectSequenceStatement statement) throws SQLException {
	    final Long r = execute(conn, statement, StandardSelectListItem.ITEM);
	    return r == null ? 0 : r.longValue();
	}

	@CheckForNull
	public <T> T execute(final Connection conn, final SelectSequenceStatement statement, final SelectListItem<T> item) throws SQLException {
	    final SequenceDef sequence = statement.getSequence();

	    final String sqlStr= _sqlGenerator.generateSequenceNextvalCmd(_dialect, sequence.getSequenceName());
	    final PreparedStatement preparedStmt= conn.prepareStatement( sqlStr);
	    final ResultSet rs= preparedStmt.executeQuery();

	    final SelectResult r = new SqlResultImpl( _dialect, new SelectListItem<?>[] {item}, rs, preparedStmt);
	    try {
    	    if (r.hasNext()) {
    	        final SqlResultRow row = r.next();
    	        return row.get(item);
    	    }
	    } finally {
	        r.close();
	    }
	    // if there is no result, tell the caller about it by returning null.
	    return null;
	}

	public String toString(final SelectSequenceStatement statement) {
	    return _sqlGenerator.generateSequenceNextvalCmd(_dialect, statement.getSequence().getSequenceName());
	}
}
