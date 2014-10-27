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

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.generate.SqlQueryType;
import com.freiheit.sqlapi4j.generate.impl.PreparedStatementData;
import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.ColumnDef;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.query.statements.InsertStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InsertStatementExecutor extends AbstractQueryStatementExecutor<InsertStatement, InsertStatement.Result> {

	public InsertStatementExecutor(final SqlQueryType queryType, final SqlDialect sqlDdialect, final SqlGenerator sqlGenerator) {
	    super(queryType, sqlDdialect, sqlGenerator);
	}

    @Override
    public InsertStatement.Result execute(final Connection conn, final InsertStatement statement) throws SQLException {
        return execute(conn, statement, null);
    }

    public <I> InsertStatement.Result<I> execute(final Connection conn, final InsertStatement statement, @Nullable final ColumnDef<I> idCol) throws SQLException {
        switch( getSqlQueryType()) {
            case PREPARED:
                return executePrepared(conn, statement, idCol);
            default:
                return executePlainSql(conn, statement, idCol);
        }
    }

    @Nonnull
    private <I> InsertStatement.Result<I> executePlainSql(final Connection conn, final InsertStatement statement, @Nullable final ColumnDef<I> idCol) throws SQLException {
        final String insertStr = generateStatement(PreparedStatementData.NO_PREPARED_STATEMENT, statement);
        final Statement stmt= conn.createStatement();
        try {
            int nofRowsUpdated = stmt.executeUpdate(insertStr);
            return getResultExtractingGeneratedKeys(stmt, nofRowsUpdated, idCol);
        }
        finally {
            stmt.close();
        }
    }

    @Nonnull
    private <I> InsertStatement.Result<I> executePrepared(final Connection conn, final InsertStatement statement, @Nullable final ColumnDef<I> idCol) throws SQLException {
        final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
        final String insertStr = generateStatement(preparedStatementData, statement);
        final PreparedStatement stmt= conn.prepareStatement( insertStr, Statement.RETURN_GENERATED_KEYS);
        try {
            final Iterator<?> it= preparedStatementData.getValues();
            int i= 1;
            while( it.hasNext()) {
                stmt.setObject( i, it.next());
                ++i;
            }
            int nofRowsUpdated = stmt.executeUpdate();
            return getResultExtractingGeneratedKeys(stmt, nofRowsUpdated, idCol);
        }
        finally {
            stmt.close();
        }
    }

    private String generateStatement(final PreparedStatementData preparedStatementData, final InsertStatement statement) {
        return getGenerator().generateInsertStatement(
                getDialect(), statement.getTable(), statement.getRows(), statement.getFromDefs(), statement.getWhere(), preparedStatementData
        );
    }

    @Nonnull
    private <I> InsertStatement.Result<I> getResultExtractingGeneratedKeys(final Statement stmt, final int nofRowsUpdated, @Nullable final ColumnDef<I> idCol) throws SQLException {
        final ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (idCol == null || generatedKeys == null || !generatedKeys.next()) {
            return new InsertStatementResultWithoutIds(nofRowsUpdated);
        } else {
            final ColumnConverter<I, Long> idColConverter = (ColumnConverter<I, Long>)getDialect().getConverterFor(((Class<DbType<I>>)idCol.type().getClass()));
            if( idColConverter == null) {
                throw new IllegalStateException( "no converter for column definition " + idCol + " with db type " + idCol.type().getClass());
            }
            final List<I> generatedIds = new ArrayList<I>();
            do {
                final long generatedId = generatedKeys.getLong(1);
                generatedIds.add(idColConverter.fromDb(generatedId, idCol.type()));
            } while (generatedKeys.next());
            return new InsertStatementResultWithIds(nofRowsUpdated, generatedIds);
        }
    }

    public String toString(final InsertStatement statement) {
        switch( getSqlQueryType()) {
            case PREPARED:
                final PreparedStatementData preparedStatementData= new PreparedStatementData( true);
                return generateStatement(preparedStatementData, statement) + " ; values: " + preparedStatementData;
            default:
                return generateStatement(PreparedStatementData.NO_PREPARED_STATEMENT, statement);
        }
    }

}
