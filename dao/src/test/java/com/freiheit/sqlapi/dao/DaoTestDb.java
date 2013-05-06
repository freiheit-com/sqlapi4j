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
package com.freiheit.sqlapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.freiheit.sqlapi.dao.meta.IdColumnDef;
import com.freiheit.sqlapi.domain.type.Id;
import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.meta.ColumnDefNullable;
import com.freiheit.sqlapi.meta.ColumnDefs;
import com.freiheit.sqlapi.meta.SequenceDef;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.test.hsql.TestDb;

public class DaoTestDb extends TestDb {

    public static final DaoTestDb INSTANCE = new DaoTestDb();

    public static class AddressId extends Id {

        protected AddressId(final long value) {
            super(value);
        }

    }

    public static class PersonId extends Id {

        protected PersonId(final long value) {
            super(value);
        }

    }

    public static class Address {

        public static final ColumnDef<AddressId> ID = new IdColumnDef<AddressId>("id", AddressId.class);
        public static final ColumnDefNullable<String> CITY= ColumnDefs.varcharNullable( "city");
        public static final ColumnDefNullable<String> STREET= ColumnDefs.varcharNullable( "street");
        public static final ColumnDefNullable<Integer> NUMBER= ColumnDefs.integerNullable( "number");
        public static final ColumnDefNullable<String> ZIP= ColumnDefs.varcharNullable( "zip");
        public static final ColumnDefNullable<Country> COUNTRY= ColumnDefs.enumTypeNullable( "country", Country.class);

        public static final SequenceDef SEQ= new SequenceDef( "address_seq", 1);

        public static final TableDef TABLE= new TableDef( "address", ID, CITY, STREET, NUMBER, ZIP, COUNTRY);
    }

    public static class Person {

        public static final ColumnDef<PersonId> ID = new IdColumnDef<PersonId>("id", PersonId.class);
        public static final ColumnDefNullable<String> NAME= ColumnDefs.varcharNullable( "name");
        public static final ColumnDefNullable<String> LASTNAME= ColumnDefs.varcharNullable( "lastname");
        public static final ColumnDefNullable<Integer> HEIGTH= ColumnDefs.integerNullable( "height");
        public static final ColumnDefNullable<Calendar> BIRTH_DATE= ColumnDefs.timestampNullable( "birth_date");
        public static final ColumnDefNullable<Gender> GENDER= ColumnDefs.enumTypeNullable( "gender", Gender.class);

        public static final TableDef TABLE= new TableDef( "person", ID, NAME, LASTNAME, HEIGTH, BIRTH_DATE, GENDER);
        public static final SequenceDef SEQ= new SequenceDef( "address_seq", 1);
    }

    @Override
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

    @Override
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

}
