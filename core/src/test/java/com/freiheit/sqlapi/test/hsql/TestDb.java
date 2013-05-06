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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.meta.ColumnDefNullable;
import com.freiheit.sqlapi.meta.ColumnDefs;
import com.freiheit.sqlapi.meta.SequenceDef;
import com.freiheit.sqlapi.meta.TableDef;

public class TestDb {

    public static final TestDb INSTANCE = new TestDb();

	//  private static final String JDBC_DRIVER= "org.hsqldb.jdbcDriver";
	//	private static final String JDBC_CONN_STRING= "jdbc:hsqldb:mem:mymemdb";
	//	private static final String JDBC_UNAME= "SA";
	//	private static final String JDBC_PWD= "";

	protected static final String JDBC_DRIVER= "org.h2.Driver";
	protected static final String JDBC_CONN_STRING= "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	protected static final String JDBC_UNAME= "sa";
	protected static final String JDBC_PWD= "";

	private static final String ADDRESS_DDL_STR= "create table address ( id integer not null primary key, city varchar(256), street varchar(256), number integer, zip varchar(256), country varchar(256));";
	private static final String ADDRESS_CREATE_SEQUENCE_STR= "create sequence address_seq";
	private static final String ADDRESS_DROP_STR= "drop table address";
	private static final String ADDRESS_DROP_SEQUENCE_STR= "drop sequence address_seq";
	protected static final String ADDRESS_INSERT_STR= "insert into address ( id, city, street, number, zip, country) values (?,?,?,?,?,?);";

	private static final String PERSON_DDL_STR= "create table person ( id integer not null primary key, name varchar(256), lastname varchar(256), height integer, birth_date timestamp(0), gender varchar(256));";
	private static final String PERSON_CREATE_SEQUENCE_STR= "create sequence person_seq";
	private static final String PERSON_DROP_STR= "drop table person";
	private static final String PERSON_DROP_SEQUENCE_STR= "drop sequence person_seq";
	protected static final String PERSON_INSERT_STR= "insert into person ( id, name, lastname, height, birth_date, gender) values (?,?,?,?,?,?);";

	public static enum Gender {
		MALE,
		FEMALE,
		UNDEFINED;
	}

	public static enum Country {
		DE,
		CH,
		AT;
	}

	public static interface DbOperation<T> {
		T execute( Connection c) throws SQLException;
	}

	public static class Address {

		public static final ColumnDef<Long> ID= ColumnDefs.longT( "id");
		public static final ColumnDefNullable<String> CITY= ColumnDefs.varcharNullable( "city");
		public static final ColumnDefNullable<String> STREET= ColumnDefs.varcharNullable( "street");
		public static final ColumnDefNullable<Integer> NUMBER= ColumnDefs.integerNullable( "number");
		public static final ColumnDefNullable<String> ZIP= ColumnDefs.varcharNullable( "zip");
		public static final ColumnDefNullable<Country> COUNTRY= ColumnDefs.enumTypeNullable( "country", Country.class);

		public static final SequenceDef SEQ= new SequenceDef( "address_seq", 1);

		public static final TableDef TABLE= new TableDef( "address", ID, CITY, STREET, NUMBER, ZIP, COUNTRY);
	}

	public static class Person {

		public static final ColumnDef<Long> ID= ColumnDefs.longT( "id");
		public static final ColumnDefNullable<String> NAME= ColumnDefs.varcharNullable( "name");
		public static final ColumnDefNullable<String> LASTNAME= ColumnDefs.varcharNullable( "lastname");
		public static final ColumnDefNullable<Integer> HEIGTH= ColumnDefs.integerNullable( "height");
		public static final ColumnDefNullable<Calendar> BIRTH_DATE= ColumnDefs.timestampNullable( "birth_date");
		public static final ColumnDefNullable<Gender> GENDER= ColumnDefs.enumTypeNullable( "gender", Gender.class);

		public static final TableDef TABLE= new TableDef( "person", ID, NAME, LASTNAME, HEIGTH, BIRTH_DATE, GENDER);
	}

    public TestDb() {
        super();
    }

	public void setupDb() throws SQLException {
		execute( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final Statement pstmt= c.createStatement();
				pstmt.execute( PERSON_DDL_STR);
				final Statement astmt= c.createStatement();
				astmt.execute( ADDRESS_DDL_STR);
				final Statement asstmt= c.createStatement();
				asstmt.execute( ADDRESS_CREATE_SEQUENCE_STR);
				final Statement psstmt= c.createStatement();
				psstmt.execute( PERSON_CREATE_SEQUENCE_STR);
				return null;
			}
		});
	}

	public void dropDb() throws SQLException {
	    execute( new DbOperation<Void>() {
	        @Override
	        public Void execute(final Connection c) throws SQLException {
	            final Statement pstmt= c.createStatement();
	            pstmt.execute( PERSON_DROP_STR);
	            final Statement astmt= c.createStatement();
	            astmt.execute( ADDRESS_DROP_STR);
	            final Statement psstmt= c.createStatement();
	            psstmt.execute( PERSON_DROP_SEQUENCE_STR);
	            final Statement asstmt= c.createStatement();
	            asstmt.execute( ADDRESS_DROP_SEQUENCE_STR);
	            return null;
	        }
	    });
	}

	public void insertTestAddresses() {
		insertTestAddress( 100l, "Berlin", "Unter den Linden", 22, "10001", Country.DE);
	}

	protected void insertTestAddress( final long id, final String city, final String street, final int number, final String zip, final Country country) {
		executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final PreparedStatement ps= c.prepareStatement( ADDRESS_INSERT_STR);
				ps.setLong( 1, id);
				ps.setString( 2, city);
				ps.setString( 3, street);
				ps.setInt( 4, number);
				ps.setString( 5, zip);
				ps.setString( 6, country.name());
				ps.execute();
				return null;
			}
		});
	}

	public void insertTestPersons() {
		insertPerson( 1, "Peter", "Pan", 178, new Date(), Gender.MALE);
		insertPerson( 2, "Paul", "Panzer", 170, new Date(), Gender.MALE);
		insertPerson( 3, "Mary", "Poppins", 158, new Date(), Gender.FEMALE);
		insertPerson( 4, "Ford", "Prefect", 158, new Date(), Gender.MALE);
		insertPerson( 5, "Hans", "Wurst", 170, new Date(), Gender.MALE);
		insertPerson( 6, "Harry", "Potter", 170, new Date(), Gender.MALE);
		insertPerson( 7, "Berlin", "City", 173, new Date(), Gender.MALE);
		insertPerson( 8, "David", "david", 174, new Date(), Gender.MALE);
	}

	protected void insertPerson( final long id, final String name, final String lastName, final int height, final Date birthDate, final Gender gender) {
		executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final PreparedStatement ps= c.prepareStatement( PERSON_INSERT_STR);
				ps.setLong( 1, id);
				ps.setString( 2, name);
				ps.setString( 3, lastName);
				ps.setInt( 4, height);
				ps.setTimestamp( 5, new Timestamp( birthDate.getTime()));
				ps.setString( 6, gender.name());
				ps.execute();
				return null;
			}
		});
	}

	public <T> T execute( final DbOperation<T> op) throws SQLException {
		Connection c= null;
		try {
			c= DriverManager.getConnection( JDBC_CONN_STRING, JDBC_UNAME, JDBC_PWD);
			return op.execute( c);
		} finally {
			if( c != null) {
                c.close();
            }
		}
	}

	public <T> T executeSingle( final DbOperation<T> op) {
		Connection c= null;
		try {
			c= DriverManager.getConnection( JDBC_CONN_STRING, JDBC_UNAME, JDBC_PWD);
			final T res = op.execute( c);
			c.commit();
			return res;
		} catch( final Exception e) {
			e.printStackTrace();
			throw new RuntimeException( "DB-Fehler", e);
		} finally {
			if( c != null) {
                try {
                	c.close();
                } catch( final SQLException e) {
                	e.printStackTrace();
                	throw new RuntimeException( "DB-Fehler", e);
                }
            }
		}
	}

}
