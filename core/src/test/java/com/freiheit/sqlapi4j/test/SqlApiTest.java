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
package com.freiheit.sqlapi4j.test;

import static com.freiheit.sqlapi4j.query.Sql.and;
import static com.freiheit.sqlapi4j.query.Sql.or;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.freiheit.sqlapi4j.generate.impl.ConverterRegistry;
import com.freiheit.sqlapi4j.generate.impl.PostgresSqlDialect;
import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.ColumnDefNullable;
import com.freiheit.sqlapi4j.meta.ColumnDefs;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.InsertCommand;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlBuilder;
import com.freiheit.sqlapi4j.query.SqlCommand;
import com.freiheit.sqlapi4j.query.SqlExecutor;
import com.freiheit.sqlapi4j.query.SqlResultRow;
import com.freiheit.sqlapi4j.query.UpdateCommand;
import com.freiheit.sqlapi4j.query.impl.SqlBuilderImpl;
import com.freiheit.sqlapi4j.query.impl.SqlExecutorImpl;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;

public class SqlApiTest {
	private static final SqlExecutor EXEC = new SqlExecutorImpl( new PostgresSqlDialect( new ConverterRegistry( null).registerConverter( TimestampDate.class, new TimstampDateConverter())));
	private static final SqlBuilder SQL= new SqlBuilderImpl();

	enum Test {
		TEST1,
		TEST2;
	}

	private static class TimestampDate implements DbType<Long> {
	}

	private static class TimstampDateConverter implements ColumnConverter<Long,java.sql.Date> {

		@Override
		public int getSqlType() {
			return Types.DATE;
		}

		@Override
		public Long fromDb( final Date value, final DbType<Long> clazz) {
			return value.getTime();
		}

		@Override
		public Date toDb( final Long value, final DbType<Long> clazz) {
			return new java.sql.Date( value.longValue());
		}

		@Override
		public String getSqlTypeDeclaration( final DbType<Long> dbType) {
			return "TIMESTAMP";
		}

	}

	public static class PersonT {

		public static final ColumnDefNullable<Integer> AGE= ColumnDefs.integerNullable( "AGE");
		public static final ColumnDefNullable<String> NAME= ColumnDefs.varcharNullable( "NAME");
		public static final ColumnDefNullable<String> LASTNAME= ColumnDefs.varcharNullable( "LAST_NAME");
		public static final ColumnDefNullable<Test> TEST= ColumnDefs.enumTypeNullable( "TEST_ENUM", Test.class);

		public static final TableDef TABLE= new TableDef( "PERSON", AGE, NAME, LASTNAME);
	}

	public static void queryTest() {

		final SqlCommand<SelectStatement> query1= SQL.select( PersonT.AGE).from( PersonT.TABLE).where( PersonT.AGE.eq( 42), or( PersonT.NAME.eq( "P'eter"), and( PersonT.NAME.isNull(), PersonT.LASTNAME.eq( "M'eier"))));
		System.out.println( "query:\n" + EXEC.render(query1.stmt()));

		final SqlCommand<SelectStatement> query2= SQL.select( PersonT.AGE).from( PersonT.TABLE).where( PersonT.TEST.eq( Test.TEST1));
		System.out.println( "query:\n" + EXEC.render(query2.stmt()));

	}

	/**
	 * Erstelle die Tabelle mit <code>
	 * create table Release (
	 *     rel_pk bigserial primary key,
	 *     rel_name varchar(255),
	 *     rel_date timestamp with time zone not null default now(),
	 *     rel_state integer,
	 *     rel_days integer,
	 *     rel_min integer,
	 *     rel_max integer,
	 *     rel_phase_id integer
	 * );
     * </code>
	 */
	private static class Release {

		public static final ColumnDefNullable<Integer> PK= ColumnDefs.integerNullable( "rel_pk");
		public static final ColumnDefNullable<String> NAME= ColumnDefs.varcharNullable( "rel_name");
		// public static final ColumnDef<Calendar> DATE= ColumnDefs.dateTime("rel_date");
		public static final ColumnDefNullable<Long> DATE= ColumnDefNullable.of( "rel_date", new TimestampDate());
		public static final ColumnDefNullable<Integer> STATE= ColumnDefs.integerNullable( "rel_state");
		public static final ColumnDefNullable<Integer> DAYS= ColumnDefs.integerNullable( "rel_days");
		public static final ColumnDefNullable<Integer> MIN= ColumnDefs.integerNullable( "rel_min");
		public static final ColumnDefNullable<Integer> MAX= ColumnDefs.integerNullable( "rel_max");
		public static final ColumnDefNullable<Integer> PHASE_ID= ColumnDefs.integerNullable( "rel_phase_id");

		public static final TableDef TABLE= new TableDef( "release", PK, NAME, DATE, STATE, DAYS, MIN, MAX, PHASE_ID);
	}

	private static class Phase {
		public static final ColumnDefNullable<Integer> PK= ColumnDefs.integerNullable( "phs_pk");
		public static final ColumnDefNullable<String> NAME= ColumnDefs.varcharNullable( "phs_name");
		public static final ColumnDefNullable<Integer> STATE= ColumnDefs.integerNullable( "phs_state");

		public static final TableDef TABLE= new TableDef( "phase", PK, NAME, STATE);
	}

	public static void resultSetTest( final Connection conn) throws SQLException {

		final SqlCommand<SelectStatement> query= SQL.select( Release.PK, Release.NAME, Release.STATE, Release.DATE).from( Release.TABLE).where( Release.PK.lt( 20)).orderBy( Release.PK);

		System.out.println( "query: " + query);
		System.out.println();

		final SelectResult result= execute(conn, query);

		while( result.hasNext()) {
			final SqlResultRow row= result.next();
			System.out.println( "pk=" + row.get( Release.PK) + " name=" + row.get( Release.NAME) + " state=" + row.get( Release.STATE) + " timstamp=" + row.get( Release.DATE));
		}
	}

    private static SelectResult execute(final Connection conn, final SqlCommand<SelectStatement> query) throws SQLException {
        return EXEC.execute(conn, query.stmt());
    }

	public static void resultSetTestGroupCount( final Connection conn) throws SQLException {

		final SqlCommand<SelectStatement> query= SQL.select( Release.NAME, Release.STATE, Sql.count( Release.PK)).from( Release.TABLE).where( Release.PK.lt( 20)).groupBy( Release.NAME, Release.STATE).orderBy( Release.NAME);

		System.out.println( "query: " + query);
		System.out.println();

		final SelectResult result= execute(conn, query);

		while( result.hasNext()) {
			final SqlResultRow row= result.next();
			System.out.println( "name=" + row.get( Release.NAME) + " state=" + row.get( Release.STATE) + " count=" + row.get( Sql.count( Release.PK)));
		}
	}

	// SelectListItem.ALL wird erst mal nicht weiter unterstützt
	//	public static void testQueryAllCols( DataSource ds) {
	//		System.out.println( "------------ starting testQueryAllCols --------------------\n");
	//		SqlQuery query= SQL.select( SelectListItem.ALL).from( Release.TABLE).where( Release.PK.lt( 20)).orderBy( Release.PK);
	//		doTestQuery( ds, query);
	//		System.out.println( "------------ finished testQueryAllCols --------------------\n");
	//	}

	public static void testQueryCount( final DataSource ds) {
		System.out.println( "------------ starting testQueryCount --------------------\n");
		final SqlCommand<SelectStatement> query= SQL.select( Sql.count( Release.PK)).from( Release.TABLE).where( Release.PK.lt( 20));
		doTestQuery( ds, query);
		System.out.println( "------------ finished testQueryCount --------------------\n");
	}

	public static void testGroupCount( final DataSource ds) {
		System.out.println( "------------ starting testGroupCount --------------------\n");

		final SqlCommand<SelectStatement> query= SQL.select( Release.NAME, Release.STATE, Sql.count( Release.PK), Sql.min( Release.PK)).from( Release.TABLE).where( Release.PK.lt( 20)).groupBy( Release.NAME, Release.STATE).orderBy( Release.NAME).limit( 2);

		doTestQuery( ds, query);
		System.out.println( "------------ finished testGroupCount --------------------\n");
	}

	public static void testWhereIn( final DataSource ds) {
		System.out.println( "\n------------ starting testWhereIn --------------------\n");

		final SqlCommand<SelectStatement> query= SQL.select( Release.PK, Release.NAME, Release.STATE).from( Release.TABLE).where( Release.PK.in( 10, 11, 12, 13, 14));
		//SqlQuery query= Sql.select( Release.PK, Release.NAME, Release.STATE).from( Release.TABLE).where( Release.NAME.in( "Release 0.9", "Wettbewerbstool", "4.01"));

		doTestQuery( ds, query);
		System.out.println( "\n------------ finished testWhereIn --------------------\n");
	}

	public static void testSubselect( final DataSource ds) {
		System.out.println( "\n------------ starting testSubselect --------------------\n");

		final SqlCommand<SelectStatement> query= SQL.select( Release.PK, Release.NAME).from( Release.TABLE).where( Release.PHASE_ID.in( SQL.subSelect( Phase.PK).from( Phase.TABLE).where( Phase.NAME.in( "7.0", "3.1", "5.2a"))));

		doTestQuery( ds, query);
		System.out.println( "\n------------ finished testSubselect --------------------\n");
	}

	public static void testInsertUpdate() {
		System.out.println( "\n------------ starting testInsert --------------------\n");

		final InsertCommand insert= SQL.insert( PersonT.TABLE).values( PersonT.AGE.set( 40), PersonT.NAME.set( "Pe'ter"), PersonT.LASTNAME.set( "Pan"));

		System.out.println( insert);

		final UpdateCommand update= SQL.update( PersonT.TABLE).values( PersonT.NAME.set( "P'aul"), PersonT.AGE.set( 30)).where( PersonT.LASTNAME.eq( "Pan"));

		System.out.println( update);
		System.out.println( "\n------------ finished testInsert --------------------\n");
	}

	private static void doTestQuery( final DataSource ds, final SqlCommand<SelectStatement> query) {
		System.out.println( "query: " + query);
		System.out.println();

		try {
			final Connection conn= ds.getConnection();
			try {
				final SelectResult result= execute(conn, query);
				printResult( result);
			} catch( final SQLException e) {
				e.printStackTrace();
			} finally {
				if( conn != null) {
                    conn.close();
                }
			}
		} catch( final SQLException e1) {
			e1.printStackTrace();
		}

	}

	private static void printResult( final SelectResult result) throws SQLException {
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

	public static void main( final String[] args) {
		System.out.println( "------------ starting tests --------------------\n");
		final PGSimpleDataSource ds= new PGSimpleDataSource();
		ds.setDatabaseName( "reporting");
		ds.setServerName( "127.0.0.1");
		ds.setUser( "reporting");
		ds.setPassword( "reporting");

		doResultTest( ds);
		//testQueryAllCols( ds);
		testQueryCount( ds);
		testGroupCount( ds);
		testWhereIn( ds);
		testSubselect( ds);

		doResultTestGC( ds);

		testInsertUpdate();

		queryTest();

		System.out.println( "\n------------ finished tests --------------------");

	}

	private static void doResultTest( final PGSimpleDataSource ds) {
		try {
			final Connection conn= ds.getConnection();
			try {
				resultSetTest( ds.getConnection());
			} catch( final SQLException e) {
				e.printStackTrace();
			} finally {
				if( conn != null) {
                    conn.close();
                }
			}
		} catch( final SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void doResultTestGC( final PGSimpleDataSource ds) {
		try {
			final Connection conn= ds.getConnection();
			try {
				resultSetTestGroupCount( ds.getConnection());
			} catch( final SQLException e) {
				e.printStackTrace();
			} finally {
				if( conn != null) {
                    conn.close();
                }
			}
		} catch( final SQLException e1) {
			e1.printStackTrace();
		}
	}
}
