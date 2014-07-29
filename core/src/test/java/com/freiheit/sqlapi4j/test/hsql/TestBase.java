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
package com.freiheit.sqlapi4j.test.hsql;

import com.freiheit.sqlapi4j.generate.impl.ConverterRegistry;
import com.freiheit.sqlapi4j.generate.impl.H2DbDialect;
import com.freiheit.sqlapi4j.meta.ColumnDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlBuilder;
import com.freiheit.sqlapi4j.query.SqlCommand;
import com.freiheit.sqlapi4j.query.SqlExecutor;
import com.freiheit.sqlapi4j.query.SqlResultRow;
import com.freiheit.sqlapi4j.query.impl.SqlBuilderImpl;
import com.freiheit.sqlapi4j.query.impl.SqlExecutorImpl;
import com.freiheit.sqlapi4j.query.statements.CreateTableStatement;
import com.freiheit.sqlapi4j.query.statements.InsertStatement;
import com.freiheit.sqlapi4j.query.statements.SelectSequenceStatement;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;
import com.freiheit.sqlapi4j.query.statements.UpdateStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestBase {

	protected static final SqlExecutor EXEC= new SqlExecutorImpl( new H2DbDialect( new ConverterRegistry( null)));
	protected static final SqlBuilder SQL= new SqlBuilderImpl();

	protected static SelectResult executeQuery(final Connection conn, final SqlCommand<SelectStatement> query) throws SQLException {
        return EXEC.execute(conn, query.stmt());
    }

	protected static Integer executeInsert(final Connection conn, final SqlCommand<InsertStatement> query) throws SQLException {
	    return EXEC.execute(conn, query.stmt(), null).getNofRowsInserted();
	}

	protected static <I> InsertStatement.Result<I> executeInsert(final Connection conn, final SqlCommand<InsertStatement> query, final ColumnDef<I> idCol) throws SQLException {
	    return EXEC.execute(conn, query.stmt(), idCol);
	}

	protected static Integer executeUpdate(final Connection conn, final SqlCommand<UpdateStatement> query) throws SQLException {
	    return EXEC.execute(conn, query.stmt());
	}

    protected static long nextVal(final Connection conn, final SqlCommand<SelectSequenceStatement> query) throws SQLException {
        return EXEC.execute(conn, query.stmt());
    }

    protected static void executeCreate(final Connection conn, final SqlCommand<CreateTableStatement> query) throws SQLException {
        EXEC.execute(conn, query.stmt());
    }

    @BeforeClass
    public void setUp() throws SQLException {
        TestDb.INSTANCE.setupDb();
        TestDb.INSTANCE.insertTestAddresses();
        TestDb.INSTANCE.insertTestPersons();
    }

    @AfterClass
    public void tearDown() throws SQLException {
        TestDb.INSTANCE.dropDb();
    }

	protected static BooleanExpression and( final BooleanExpression... exprList) {
		return Sql.and( exprList);
	}

	protected static BooleanExpression or( final BooleanExpression... exprList) {
		return Sql.or( exprList);
	}

	protected void assertResultSet( final SelectResult res, final Object[][] expected, final String msg) {
		assertResultSet( res, Arrays.asList( expected), msg);
	}

	protected void assertResultSet( final SelectResult res, final Iterable<Object[]> expectedIterable, final String msg) {
		try {
			final Iterator<Object[]> expected= expectedIterable.iterator();
			if( !res.hasNext()) {
				if( !expected.hasNext()) {
					return;
				}
				Assert.fail( "result is empty, expected: " + Arrays.asList( expected.next()));
			}
			if( !expected.hasNext()) {
				Assert.fail( "expected is empty, result: " + getRowStr( res.next()));
			}
			do {
				assertEquals( res.next(), expected.next(), msg);
				if( expected.hasNext() ^ res.hasNext()) {
					if( expected.hasNext()) {
						Assert.fail( "expected has more rows: " + Arrays.asList( expected.next()));
					}
					Assert.fail( "result has more rows: " + getRowStr( res.next()));
				}
			} while( expected.hasNext() && res.hasNext());
		} catch( final SQLException e) {
			e.printStackTrace();
		}
	}

    protected void assertEquals( final SqlResultRow sqlResultRow, final Object[] expected, final String msg) {
        printComparison( sqlResultRow, expected);
        final Iterator<?> cols= sqlResultRow.columns().iterator();
        for( int i= 0; i < expected.length; ++i) {
            if( !cols.hasNext()) {
                failRow( sqlResultRow, expected, msg);
            }
            final Object value= cols.next();
            if ((value == null) != (expected[i] == null) ||
                (value != null &&  !value.equals(expected[i]))
            ) {
                failRow( sqlResultRow, expected, msg);
            }
        }
        if( cols.hasNext()) {
            failRow( sqlResultRow, expected, msg);
        }
    }

	private static void failRow( final SqlResultRow sqlResultRow, final Object[] expected, final String msg) {
		Assert.fail( msg + " expected " + Arrays.asList( expected) + " found " + sqlResultRow);
	}

	private static void printComparison( final SqlResultRow sqlResultRow, final Object[] expected) {
		System.out.println( "expected " + Arrays.asList( expected) + " found " + sqlResultRow);
	}

	private static String getRowStr( final SqlResultRow sqlResultRow) {
		return sqlResultRow.toString();
	}

	protected static void printResult( final SelectResult result) throws SQLException {
		for( final String item : result.columnDefs()) {
			System.out.print( item + "\t");
		}
		System.out.println( "");

		while( result.hasNext()) {
			final SqlResultRow row= result.next();
			for( final Object item : row.columns()) {
				System.out.print( "" + item + "\t");
			}
			System.out.println( "");
		}
	}

}
