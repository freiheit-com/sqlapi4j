package com.freiheit.sqlapi4j.demo.dao;

import com.freiheit.sqlapi4j.dao.meta.IdColumnDef;
import com.freiheit.sqlapi4j.demo.model.Gender;
import com.freiheit.sqlapi4j.demo.model.PersonId;
import com.freiheit.sqlapi4j.meta.ColumnDef;
import com.freiheit.sqlapi4j.meta.ColumnDefNullable;
import com.freiheit.sqlapi4j.meta.ColumnDefs;
import com.freiheit.sqlapi4j.meta.SequenceDef;
import com.freiheit.sqlapi4j.meta.TableDef;

public class PersonTbl {

    public static final ColumnDef<PersonId> ID = new IdColumnDef<PersonId>("id", PersonId.class);
    public static final ColumnDefNullable<String> FIRST_NAME= ColumnDefs.varcharNullable( "name");
    public static final ColumnDefNullable<String> LAST_NAME= ColumnDefs.varcharNullable( "lastname");
    public static final ColumnDefNullable<Integer> HEIGTH= ColumnDefs.integerNullable( "height");
    public static final ColumnDefNullable<Gender> GENDER= ColumnDefs.enumTypeNullable( "gender", Gender.class);

    public static final TableDef TABLE= new TableDef( "person", ID, FIRST_NAME, LAST_NAME, HEIGTH, GENDER);
    public static final SequenceDef SEQ= new SequenceDef( "person_seq", 1);
}