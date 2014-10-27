package com.freiheit.sqlapi4j.demo.dao;

import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.FIRST_NAME;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.GENDER;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.HEIGTH;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.ID;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.LAST_NAME;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.SEQ;
import static com.freiheit.sqlapi4j.demo.dao.PersonTbl.TABLE;

import java.util.List;

import com.freiheit.sqlapi4j.dao.ResultTransformer;
import com.freiheit.sqlapi4j.dao.SingleTableDao;
import com.freiheit.sqlapi4j.demo.model.Gender;
import com.freiheit.sqlapi4j.demo.model.Person;
import com.freiheit.sqlapi4j.demo.model.PersonId;
import com.freiheit.sqlapi4j.domain.type.TransactionTemplate;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlExecutor;
import com.freiheit.sqlapi4j.query.SqlResultRow;
import com.freiheit.sqlapi4j.tx.Transactional;
import com.google.inject.Inject;

public class PersonDao extends SingleTableDao<PersonId, Person> {
	public static final ResultTransformer<Person> TRANSFORMER = new ResultTransformer<Person>() {

        @Override
        public Person apply(final SqlResultRow row) {
            return new Person(
                row.get(PersonTbl.ID),
                row.get(PersonTbl.FIRST_NAME),
                row.get(PersonTbl.LAST_NAME),
                row.get(PersonTbl.HEIGTH),
                row.get(PersonTbl.GENDER)
            );
        }
	};

	@Inject
	public PersonDao(
			TransactionTemplate transactionTemplate,
			SqlExecutor sqlExecutor
	) {
		super(transactionTemplate, sqlExecutor, TABLE, ID, TRANSFORMER);
	}
	
	@Transactional
	public void insertPerson(String firstname, String lastname, int height, Gender gender) {
		insert(
			// Sequence to use for Ids
			SEQ,
			// Column values
			FIRST_NAME.set(firstname),
			LAST_NAME.set(lastname),
			HEIGTH.set(height),
			GENDER.set(gender)
		);
	}
	
	public List<Person> findByName(String firstname, String lastname) {
		// example for a simple query on the table of this single table dao, using 
		// the column values as where criteria
		return findAll(FIRST_NAME.eq(firstname), LAST_NAME.eq(lastname));
	}
	
	public List<Person> findByNameFullSqlApiUsingImplementation(String firstname, String lastname) {
		// example for a simple query on the table of this single table dao, using 
		// the column values as where criteria
		// 
		// this does roughly the same as findByName, plus limit of results and ordering
		return findAll(reader(), 
				sql().select(TABLE.getColumns())
				.from(TABLE)
				.where(Sql.and(FIRST_NAME.eq(firstname), LAST_NAME.eq(lastname)))
				.orderBy(FIRST_NAME, LAST_NAME)
				.limit(10)
			);
	}

}
