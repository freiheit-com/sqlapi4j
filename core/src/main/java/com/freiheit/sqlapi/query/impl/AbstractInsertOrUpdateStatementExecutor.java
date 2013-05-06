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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.generate.SqlDialect;
import com.freiheit.sqlapi.generate.SqlGenerator;
import com.freiheit.sqlapi.generate.SqlQueryType;
import com.freiheit.sqlapi.generate.impl.PreparedStatementData;

/**
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 *
 */
@ParametersAreNonnullByDefault
public abstract class AbstractInsertOrUpdateStatementExecutor<T> extends AbstractQueryStatementExecutor<T, Integer> {

    public AbstractInsertOrUpdateStatementExecutor(final SqlQueryType queryType, final SqlDialect dialect, final SqlGenerator generator) {
        super(queryType, dialect, generator);
    }

    @Nonnull
    protected abstract String generateStatement(final PreparedStatementData preparedStatementData, T statement);

    @Override
    public Integer execute(final Connection conn, final T statement) throws SQLException {
    	switch( getSqlQueryType()) {
    	case PREPARED:
    		return Integer.valueOf(executePrepared( conn, statement));
    	default:
    		return Integer.valueOf(executePlainSql( conn, statement));
    	}
    }

    @Nonnegative
    private int executePlainSql(final Connection conn, final T statement) throws SQLException {
    	final String insertStr = generateStatement(PreparedStatementData.NO_PREPARED_STATEMENT, statement);
    	final Statement stmt= conn.createStatement();
    	try {
    	    return stmt.executeUpdate( insertStr);
    	}
    	finally {
    	    stmt.close();
    	}
    }

    @Nonnegative
    private int executePrepared(final Connection conn, final T statement) throws SQLException {
    	final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
    	final String insertStr = generateStatement(preparedStatementData, statement);
    	final PreparedStatement stmt= conn.prepareStatement( insertStr);
    	try {
        	final Iterator<?> it= preparedStatementData.getValues();
        	int i= 1;
        	while( it.hasNext()) {
        		stmt.setObject( i, it.next());
        		++i;
        	}
    	    return stmt.executeUpdate();
    	}
    	finally {
    	    stmt.close();
    	}
    }

    public String toString(final T statement) {
    	switch( getSqlQueryType()) {
    	case PREPARED:
    		final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
    		return generateStatement(preparedStatementData, statement) + " ; values: " + preparedStatementData;
    	default:
    	    return generateStatement(PreparedStatementData.NO_PREPARED_STATEMENT, statement);
    	}
    }

}
