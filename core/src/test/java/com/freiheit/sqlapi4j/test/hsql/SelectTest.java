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

import com.freiheit.sqlapi4j.meta.TableAlias;
import java.sql.Connection;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.SelectSequenceCommand;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlCommand;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Address;
import com.freiheit.sqlapi4j.test.hsql.TestDb.DbOperation;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Gender;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Person;

public class SelectTest extends TestBase {

	private static Object[][] RESULT_SINGLE= new Object[][] { { "Ford" } };
	private static Object[][] RESULT_JOIN= new Object[][] { { "Berlin" } };
	private static Object[][] RESULT_NOT_SINGLE= new Object[][] { { "Peter" }, { "Paul" }, { "Mary" }, { "Hans" }, { "Harry" }, { "Berlin" }, {"David"}};
	//{ "Ford" },

	private static Object[][] RESULT_AGGREGATE= new Object[][] { new Object[] { "Peter" } };
	private static Object[][] RESULT_AGGREGATE_ENUM= new Object[][] { new Object[] { Gender.MALE } };
	private static Object[][] RESULT_LIKE= new Object[][] { new Object[] { "Peter", 178 }, new Object[] { "Paul", 170 } };
	private static Object[][] RESULT_IEQ= new Object[][] { new Object[] { "Peter" }};
	private static Object[][] RESULT_IEQ_COLUMN= new Object[][] { new Object[] { "David" }};
	private static Object[][] RESULT_GT_COLUMN= new Object[][] { new Object[] { "Berlin" }, new Object[] { "David" }, new Object[] { "Ford" }, new Object[] { "Hans" }, new Object[] { "Harry" }, new Object[] { "Mary" }};
	private static Object[][] RESULT_GE_COLUMN= new Object[][] { new Object[] { "Berlin" }, new Object[] { "David" }, new Object[] { "Ford" }, new Object[] { "Hans" }, new Object[] { "Harry" }, new Object[] { "Mary" }};
	private static Object[][] RESULT_LT_COLUMN= new Object[][] { new Object[] { "Paul" }, new Object[] { "Peter" }};
	private static Object[][] RESULT_LE_COLUMN= new Object[][] { new Object[] { "Paul" }, new Object[] { "Peter" }};
	private static Object[][] RESULT_MULTIPLE= new Object[][] { new Object[] { "Mary", 158 }, new Object[] { "Ford", 158 } };
	private static Object[][] RESULT_WHERE_IN= new Object[][] { new Object[] { "Mary", 158, Gender.FEMALE }, new Object[] { "Ford", 158, Gender.MALE } };
	private static Object[][] RESULT_ALIAS= new Object[][] { new Object[] { "Ford", 4L, 1L } };
	private static Object[][] RESULT_GROUP= new Object[][] { new Object[] { 158, 2l }, new Object[] { 170, 3l }, new Object[] { 173, 1l }, new Object[] {174, 1l}, new Object[] { 178, 1l } };
	private static Object[][] RESULT_SUM= new Object[][] { new Object[] { 158, 7l }, new Object[] { 170, 13l }, new Object[] { 173, 7l }, new Object[] {174, 8l}, new Object[] { 178, 1l } };
	private static Object[][] RESULT_SUBSELECT= new Object[][] { new Object[] { "Paul", 170 }, new Object[] { "Hans", 170 }, new Object[] { "Harry", 170 } };

	@Test
	public void testSelectWhereIn() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME, Person.HEIGTH, Person.GENDER).from( Person.TABLE).where( Person.LASTNAME.in( "Prefect", "Poppins")).orderBy( Person.ID);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_WHERE_IN, "test-where-in");
				return null;
			}
		});
	}

	@Test
	public void testSelectAlias() {
        final TableAlias alias = Person.TABLE.alias("a");
        final SqlCommand<SelectStatement> query= SQL.select( alias.forColumn(Person.NAME), alias.forColumn(Person.ID), Sql.count(alias.forColumn(Person.ID))).from(alias).where( alias.forColumn(Person.LASTNAME).eq("Prefect"));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_ALIAS, "test-alias");
				return null;
			}
		});
	}

    @Test
    public void testAliasCaching() {
        final TableAlias alias = Person.TABLE.alias("a");
        Assert.assertTrue(alias.forColumn(Person.NAME) == alias.forColumn(Person.NAME));
        Assert.assertTrue(alias.forColumn(Person.ID) == alias.forColumn(Person.ID));
    }

        @Test
	public void testSubselect() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.ID.in( SQL.subSelect( Person.ID).from( Person.TABLE).where( Person.HEIGTH.eq( 170)))).orderBy( Person.ID);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_SUBSELECT, "test-subselect");
                return null;
			}
		});
	}

	@Test
	public void testSelectMultipleResults() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( or( Person.LASTNAME.eq( "Prefect"), Person.LASTNAME.eq( "Poppins"))).orderBy( Person.ID);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_MULTIPLE, "multiple-results");
                return null;
			}
		});
	}

	@Test
	public void testSelectLike() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.LASTNAME.like( "Pa%")).orderBy( Person.ID);
		System.out.println( query);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_LIKE, "like-results");
                return null;
			}
		});
	}

	@Test
	public void testSelectILike() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME, Person.HEIGTH).from( Person.TABLE).where( Person.LASTNAME.ilike( "pa%")).orderBy( Person.ID);
		System.out.println( query);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_LIKE, "like-results");
                return null;
			}
		});
	}

   @Test
    public void testSelectIeqValue() {
        final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE).where( Person.LASTNAME.ieq( "pan")).orderBy( Person.ID);
        System.out.println( query);

        TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
            @Override
            public Void execute( final Connection c) throws SQLException {
                final SelectResult res= executeQuery(c, query);
                System.out.println("result is:");
                assertResultSet( res, RESULT_IEQ, "ieq-value-results");
                return null;
            }
        });
    }

   @Test
   public void testSelectIeqColumn() {
       final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE).where( Person.LASTNAME.ieq(Person.NAME)).orderBy( Person.ID);
       System.out.println( query);

       TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
           @Override
           public Void execute( final Connection c) throws SQLException {
               final SelectResult res= executeQuery(c, query);
               System.out.println("result is:");
               assertResultSet( res, RESULT_IEQ_COLUMN, "ieq-column-results");
               return null;
           }
       });
   }

   @Test
   public void testSelectGtColumn() {
       final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE ).where( Person.LASTNAME.gt(Person.NAME)).orderBy( Person.NAME);
       System.out.println( query);

       TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
           @Override
           public Void execute( final Connection c) throws SQLException {
               final SelectResult res= executeQuery(c, query);
               System.out.println("result is:");
               assertResultSet( res, RESULT_GT_COLUMN, "gt-column-results");
               return null;
           }
       });
   }

   @Test
   public void testSelectGeColumn() {
       final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE ).where( Person.LASTNAME.ge(Person.NAME)).orderBy( Person.NAME);
       System.out.println( query);

       TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
           @Override
           public Void execute( final Connection c) throws SQLException {
               final SelectResult res= executeQuery(c, query);
               System.out.println("result is:");
               assertResultSet( res, RESULT_GE_COLUMN, "ge-column-results");
               return null;
           }
       });
   }

   @Test
   public void testSelectLtColumn() {
       final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE ).where( Person.LASTNAME.lt(Person.NAME)).orderBy( Person.NAME);
       System.out.println( query);

       TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
           @Override
           public Void execute( final Connection c) throws SQLException {
               final SelectResult res= executeQuery(c, query);
               System.out.println("result is:");
               assertResultSet( res, RESULT_LT_COLUMN, "lt-column-results");
               return null;
           }
       });
   }

   @Test
   public void testSelectLeColumn() {
       final SqlCommand<SelectStatement> query= SQL.select( Person.NAME ).from( Person.TABLE ).where( Person.LASTNAME.le(Person.NAME)).orderBy( Person.NAME);
       System.out.println( query);

       TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
           @Override
           public Void execute( final Connection c) throws SQLException {
               final SelectResult res= executeQuery(c, query);
               System.out.println("result is:");
               assertResultSet( res, RESULT_LE_COLUMN, "le-column-results");
               return null;
           }
       });
   }

	@Test
	public void testGroupCount() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.HEIGTH, Sql.count( Person.ID)).from( Person.TABLE).where( Person.ID.gt( 0l)).groupBy( Person.HEIGTH).orderBy( Person.HEIGTH);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_GROUP, "group-count");
                return null;
			}
		});
	}

	@Test
	public void testGroupSum() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.HEIGTH, Sql.sum( Person.ID)).from( Person.TABLE).where( Person.ID.gt( 0l)).groupBy( Person.HEIGTH).orderBy( Person.HEIGTH);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_SUM, "group-sum");
                return null;
			}
		});
	}

	@Test
	public void testSelectSingleResult() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME).from( Person.TABLE).where( Person.LASTNAME.eq( "Prefect"));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_SINGLE, "single-result");
                return null;
			}
		});
	}

	@Test
	public void testSelectSequenceValue() {
		final SelectSequenceCommand seq= SQL.nextval( Address.SEQ);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				long res= nextVal(c, seq);
				Assert.assertEquals( res, 1, "first-sequence");
				res= nextVal(c, seq);
				Assert.assertEquals( res, 2, "second-sequence");
				res= nextVal(c, seq);
				Assert.assertEquals( res, 3, "third-sequence");
                return null;
			}
		});

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				long res= nextVal(c, seq);
				Assert.assertEquals( res, 4, "fourth-sequence");
				res= nextVal(c, seq);
				Assert.assertEquals( res, 5, "fifth-sequence");
                return null;
			}
		});
	}

	@Test
	public void testJoin() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME).from( Person.TABLE, Address.TABLE).where( Person.NAME.eq( Address.CITY));
		System.out.println( query);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_JOIN, "join-result");
                return null;
			}
		});
	}

	@Test
	public void testNotEqualsSelectSingleResult() {
		final SqlCommand<SelectStatement> query= SQL.select( Person.NAME).from( Person.TABLE).where( Person.LASTNAME.neq( "Prefect")).orderBy( Person.ID);

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_NOT_SINGLE, "not-single-result");
                return null;
			}
		});
	}

	@Test
	public void testAggregateResult() {
		final SqlCommand<SelectStatement> query= SQL.select( Sql.max( Person.NAME)).from( Person.TABLE).where( Person.GENDER.eq( Gender.MALE));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_AGGREGATE, "test-aggregate");
                return null;
			}
		});
	}

	@Test
	public void testAggregateEnum() {
		final SqlCommand<SelectStatement> query= SQL.select( Sql.max( Person.GENDER)).from( Person.TABLE).where( Person.ID.gt( 0l));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_AGGREGATE_ENUM, "test-aggregate-enum");
                return null;
			}
		});
	}

}
