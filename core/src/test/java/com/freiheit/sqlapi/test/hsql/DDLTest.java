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
package com.freiheit.sqlapi.test.hsql;

import java.sql.Connection;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.freiheit.sqlapi.meta.ColumnDefNullable;
import com.freiheit.sqlapi.meta.ColumnDefs;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.CreateTableCommand;
import com.freiheit.sqlapi.query.InsertCommand;
import com.freiheit.sqlapi.query.SelectResult;
import com.freiheit.sqlapi.query.SqlCommand;
import com.freiheit.sqlapi.query.UpdateCommand;
import com.freiheit.sqlapi.query.statements.SelectStatement;
import com.freiheit.sqlapi.test.hsql.TestDb.Country;
import com.freiheit.sqlapi.test.hsql.TestDb.DbOperation;

public class DDLTest extends TestBase {

	//private static Object[][] RESULT_INSERT= new Object[][] { new Object[] { "Hamburg", "Strassenbahnring", Country.DE } };
	private static Object[][] RESULT_UPDATE_1= new Object[][] { new Object[] { "Hamburg", "Lehmweg", 1, Country.DE } };
	private static Object[][] RESULT_UPDATE_2= new Object[][] { new Object[] { "Hamburg", "Lehmweg", 2, Country.DE } };

	public static class Address {

		public static final ColumnDefNullable<Long> ID= ColumnDefs.longNullable( "id");
		public static final ColumnDefNullable<String> CITY= ColumnDefs.varcharNullable( "city");
		public static final ColumnDefNullable<String> STREET= ColumnDefs.varcharNullable( "street");
		public static final ColumnDefNullable<Integer> NUMBER= ColumnDefs.integerNullable( "number");
		public static final ColumnDefNullable<String> ZIP= ColumnDefs.varcharNullable( "zip", 5);
		public static final ColumnDefNullable<Country> COUNTRY= ColumnDefs.enumTypeNullable( "country", Country.class);

		public static final TableDef TABLE= new TableDef( "my_own_address1", ID, CITY, STREET, NUMBER, ZIP, COUNTRY);
	}

	@Test
	public void testCreateTable() {
		final CreateTableCommand createTable= SQL.createTable( Address.TABLE);
		System.out.println( "createTable: " + createTable);

		final InsertCommand cmd= insertAddress( 2l, "Hamburg", "Lehmweg", 1, "20251", Country.DE);
		System.out.println( "insert: " + cmd);

		final SqlCommand<SelectStatement> query =
		    SQL.select( Address.CITY, Address.STREET, Address.NUMBER, Address.COUNTRY).from( Address.TABLE).where( Address.ID.eq( 2l));
		System.out.println( "query: " + query);

		final UpdateCommand update= SQL.update( Address.TABLE).values( Address.NUMBER.set( 2)).where( Address.ID.eq( 2l));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				executeCreate(c, createTable);

				final int insertCount= executeInsert(c, cmd);
				Assert.assertEquals( insertCount, 1, "Insert Count");

				final SelectResult res1= executeQuery(c, query);
				assertResultSet( res1, RESULT_UPDATE_1, "before update");

				final int updateCount= executeUpdate(c, update);
				Assert.assertEquals( updateCount, 1, "Update Count");

				final SelectResult res2= executeQuery(c, query);
				assertResultSet( res2, RESULT_UPDATE_2, "after update");

				return null;
			}
		});
	}

	private static InsertCommand insertAddress( final long id, final String city, final String street, final int num, final String zip, final Country country) {
		return SQL.insert( Address.TABLE).values( Address.ID.set( id), Address.CITY.set( city), Address.STREET.set( street), Address.NUMBER.set( num), Address.ZIP.set( zip), Address.COUNTRY.set( country));
	}
}
