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
package com.freiheit.sqlapi4j.dao;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.freiheit.sqlapi4j.dao.DaoTestDb.Address;
import com.freiheit.sqlapi4j.dao.DaoTestDb.AddressId;
import com.freiheit.sqlapi4j.dao.DaoTestDb.Person;
import com.freiheit.sqlapi4j.dao.DaoTestDb.PersonId;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlResultRow;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Gender;


/**
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 *
 */
public class SingleTableDaoTest extends DaoTestBase {

    private static final ResultTransformer<List<Object>> LIST_RESULT_TRANSFORMER = new ResultTransformer<List<Object>>() {

        @Override
        public List<Object> apply(final SqlResultRow row) {
            final List<Object> result = new LinkedList<Object>();
            for (final Object column : row.columns()) {
                result.add(column);
            }
            return result;
        }

    };


    private static List<Object> RESULT_SINGLE = toList(new Object[] { "Ford" });
    private static List<List<Object>> RESULT_JOIN = toNestedList(new Object[][] { { "Berlin" } });
    private static List<List<Object>> RESULT_NOT_SINGLE = toNestedList(new Object[][] { { "Peter" }, { "Paul" }, { "Mary" }, { "Hans" }, { "Harry" }, { "Berlin" }, {"David"}});
    //{ "Ford" },

    private static List<Object> RESULT_AGGREGATE = toList(new Object[] { "Peter" });
    private static List<Object> RESULT_AGGREGATE_ENUM = toList(new Object[] { Gender.MALE });
    private static List<List<Object>> RESULT_LIKE = toNestedList(new Object[][] { { "Peter", 178 }, { "Paul", 170 } });
    private static List<List<Object>> RESULT_MULTIPLE = toNestedList(new Object[][] { { "Mary", 158 }, { "Ford", 158 } });
    private static List<List<Object>> RESULT_WHERE_IN = toNestedList(new Object[][] { { "Mary", 158, Gender.FEMALE }, { "Ford", 158, Gender.MALE } });
    private static List<List<Object>> RESULT_GROUP = toNestedList(new Object[][] { { 158, 2l }, { 170, 3l }, { 173, 1l },{ 174, 1l} , { 178, 1l } });
    private static List<List<Object>> RESULT_SUM = toNestedList(new Object[][] { { 158, 7l }, { 170, 13l }, { 173, 7l }, {174, 8l}, { 178, 1l } });
    private static List<List<Object>> RESULT_SUBSELECT = toNestedList(new Object[][] { { "Paul", 170 }, { "Hans", 170 }, { "Harry", 170 } });

    private final SingleTableDao<PersonId, List<Object>> _personDao =
        new SingleTableDao<PersonId, List<Object>>(new TestTransactionTemplate(), EXEC, Person.TABLE, Person.ID, LIST_RESULT_TRANSFORMER) {};
    private final SingleTableDao<AddressId, List<Object>> _addressDao =
        new SingleTableDao<AddressId, List<Object>>(new TestTransactionTemplate(), EXEC, Address.TABLE, Address.ID, LIST_RESULT_TRANSFORMER) {};

    @Test
    public void testSelectWhereIn() {
        final List<List<Object>> res = _personDao.findAll(SQL.select(Person.NAME, Person.HEIGTH, Person.GENDER).from( Person.TABLE).where( Person.LASTNAME.in( "Prefect", "Poppins")).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_WHERE_IN);
    }

    @Test
    public void testSubselect() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.ID.in( SQL.subSelect( Person.ID).from( Person.TABLE).where( Person.HEIGTH.eq( 170)))).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_SUBSELECT);
    }

    @Test
    public void testSelectMultipleResults() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( or( Person.LASTNAME.eq( "Prefect"), Person.LASTNAME.eq( "Poppins"))).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_MULTIPLE);
    }

    @Test
    public void testSelectLike() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.LASTNAME.like( "Pa%")).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_LIKE);
    }

    @Test
    public void testSelectILike() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.LASTNAME.ilike( "pa%")).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_LIKE);
    }

    @Test
    public void testGroupCount() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.HEIGTH, Sql.count( Person.ID)).from( Person.TABLE).where( Person.ID.gt(new PersonId(0L))).groupBy( Person.HEIGTH).orderBy( Person.HEIGTH));
        Assert.assertEquals(res, RESULT_GROUP);
    }

    @Test
    public void testGroupSum() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.HEIGTH, Sql.sum( Person.ID)).from( Person.TABLE).where( Person.ID.gt(new PersonId(0L))).groupBy( Person.HEIGTH).orderBy( Person.HEIGTH));
        Assert.assertEquals(res, RESULT_SUM);
    }

    @Test
    public void testSelectSingleResult() {
        final List<Object> res = _personDao.findUnique(SQL.select( Person.NAME).from( Person.TABLE).where( Person.LASTNAME.eq( "Prefect")));
        Assert.assertEquals(res, RESULT_SINGLE);
    }

    @Test
    public void testSelectSequenceValue() {
        Assert.assertEquals(_addressDao.select(SQL.nextval(Address.SEQ)).longValue(), 1L, "first-sequence");
        Assert.assertEquals(_addressDao.select(SQL.nextval(Address.SEQ)).longValue(), 2L, "second-sequence");
        Assert.assertEquals(_addressDao.select(SQL.nextval(Address.SEQ)).longValue(), 3L, "third-sequence");
    }

    @Test
    public void testJoin() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME).from( Person.TABLE, Address.TABLE).where( Person.NAME.eq( Address.CITY)));
        Assert.assertEquals(res, RESULT_JOIN);
    }

    @Test
    public void testNotEqualsSelectSingleResult() {
        final List<List<Object>> res = _personDao.findAll(SQL.select( Person.NAME).from( Person.TABLE).where( Person.LASTNAME.neq( "Prefect")).orderBy( Person.ID));
        Assert.assertEquals(res, RESULT_NOT_SINGLE);
    }

    @Test
    public void testAggregateResult() {
        final List<Object> res = _personDao.findUnique(SQL.select( Sql.max( Person.NAME)).from( Person.TABLE).where( Person.GENDER.eq( Gender.MALE)));
        Assert.assertEquals(res, RESULT_AGGREGATE);
    }

    @Test
    public void testAggregateEnum() {
        final List<Object> res = _personDao.findUnique(SQL.select( Sql.max( Person.GENDER)).from( Person.TABLE).where( Person.ID.gt(new PersonId(0L))));
        Assert.assertEquals(res, RESULT_AGGREGATE_ENUM);
    }

}
