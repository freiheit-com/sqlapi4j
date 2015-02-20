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

import java.sql.Connection;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.freiheit.sqlapi4j.meta.TableAlias;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.SelectSequenceCommand;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SqlCommand;
import com.freiheit.sqlapi4j.query.impl.JoinDecl;
import com.freiheit.sqlapi4j.query.impl.OnPart;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Address;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Cat;
import com.freiheit.sqlapi4j.test.hsql.TestDb.DbOperation;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Gender;
import com.freiheit.sqlapi4j.test.hsql.TestDb.Person;

public class SelectTest extends TestBase {

	private static Object[][] RESULT_SINGLE= new Object[][] { { "Ford" } };
	private static Object[][] RESULT_JOIN= new Object[][] { { "Berlin" } };
	private static Object[][] RESULT_LEFT_OUTER_JOIN= new Object[][] { { "Berlin", "Berlin" }, { "David", null }, { "Ford", null }, { "Hans", null }, { "Harry", null }, {"Hummel", null }, { "Mary", null }, { "Paul", null }, { "Peter", null } };
	private static Object[][] RESULT_LEFT_OUTER_JOIN_WITH_ADDITIONAL_EXPR = new Object[][] { { "Berlin", null }, { "David", null }, { "Ford", null }, { "Hans", null }, { "Harry", null }, {"Hummel", null }, { "Mary", null }, { "Paul", null }, { "Peter", null } };
	private static Object[][] RESULT_MULTI_JOIN= new Object[][] { { "Berlin", "Berlin", null }, { "David", null, null }, { "Ford", null, null }, { "Hans", null, null }, { "Harry", null, null }, {"Hummel", null, null }, { "Mary", null, null }, { "Paul", null, null }, { "Peter", null, "Nyan" } };
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
	private static Object[][] RESULT_GROUP= new Object[][] { new Object[] { 158, 2l }, new Object[] { 170, 3l }, new Object[] { 173, 1l }, new Object[] {174, 1l}, new Object[] { 178, 1l }, new Object[] { 190, 1l } };
	private static Object[][] RESULT_SUM= new Object[][] { new Object[] { 158, 7l }, new Object[] { 170, 13l }, new Object[] { 173, 7l }, new Object[] {174, 8l}, new Object[] { 178, 1l }, new Object[] { 190, 9l } };
	private static Object[][] RESULT_SUBSELECT= new Object[][] { new Object[] { "Paul", 170 }, new Object[] { "Hans", 170 }, new Object[] { "Harry", 170 } };
    private static Object[][] RESULT_NULLS_FIRST = new Object[][] { { null }, { "City" }, { "Pan" }, { "Panzer" }, { "Poppins" }, { "Potter" }, { "Prefect" }, { "Wurst" }, { "david" } };
    private static Object[][] RESULT_NULLS_LAST = new Object[][] { { "City" }, { "Pan" }, { "Panzer" }, { "Poppins" }, { "Potter" }, { "Prefect" }, { "Wurst" }, { "david" }, { null } };
    private static Object[][] RESULT_AGGREGATE_ORDER = new Object[][] { { Gender.MALE, 190 }, { Gender.FEMALE, 158 } };
    private static Object[][] RESULT_ORDER_MULTIPLE = new Object[][] { { "Mary", 158 }, { "Ford", 158 }, { "Paul", 170 }, { "Harry", 170 }, { "Hans", 170 }, { "Berlin", 173 }, { "David", 174 }, { "Peter", 178 }, { "Hummel", 190 } };

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
	public void testLeftOuterJoin() {
		final SqlCommand<SelectStatement> query =
                SQL.select( Person.NAME, Address.CITY)
                   .from( Person.TABLE.leftOuterJoin(Address.TABLE).on(Person.NAME, Address.CITY))
                   .orderBy(Person.NAME);

        System.out.println( EXEC.render(query.stmt()));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_LEFT_OUTER_JOIN, "left-outer-join-result");
                return null;
			}
		});
	}

	@Test
	public void testLeftOuterJoinWithAdditionalExpression() {
		final SqlCommand<SelectStatement> query =
                SQL.select( Person.NAME, Address.CITY)
                   .from( Person.TABLE.leftOuterJoin(Address.TABLE).on(Person.NAME, Address.CITY, Address.CITY.neq("Berlin")))
                   .orderBy(Person.NAME);

        System.out.println( EXEC.render(query.stmt()));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_LEFT_OUTER_JOIN_WITH_ADDITIONAL_EXPR, "left-outer-join-with-additional-expr-result");
                return null;
			}
		});
	}

	@Test
	public void testMultiJoin() {
		FromDef join1 = Person.TABLE.leftOuterJoin(Address.TABLE).on(Person.NAME, Address.CITY);
		FromDef join2 = JoinDecl.makeLeftOuterJoin(join1, Cat.TABLE).on(Person.ID, Cat.HOLDER_ID);
		final SqlCommand<SelectStatement> query =
                SQL.select( Person.NAME, Address.CITY, Cat.NAME)
                   .from( join2)
                   .orderBy(Person.NAME);

        System.out.println( EXEC.render(query.stmt()));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, RESULT_MULTI_JOIN, "left-outer-join-result");
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
	public void testAggregateWithoutResult() {
		final SqlCommand<SelectStatement> query= SQL.select( Sql.max( Person.NAME)).from( Person.TABLE).where( Person.NAME.eq("nonexistent"));

		TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
			@Override
			public Void execute( final Connection c) throws SQLException {
				final SelectResult res= executeQuery(c, query);
				assertResultSet( res, new Object[][] { new Object[] { null }}, "test-aggregate-without-result");
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

    @Test
    public void testOrderNullsFirst() {
        final SqlCommand<SelectStatement> query = SQL.select(Person.LASTNAME).from(Person.TABLE).orderBy(Sql.asc(Person.LASTNAME).nullsFirst());
        TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
            @Override
            public Void execute( final Connection c) throws SQLException {
                final SelectResult res = executeQuery(c, query);
                assertResultSet(res, RESULT_NULLS_FIRST, "test-order-nulls-first");
                return null;
            }
        });
    }

    @Test
    public void testOrderNullsLast() {
        final SqlCommand<SelectStatement> query = SQL.select(Person.LASTNAME).from(Person.TABLE).orderBy(Sql.asc(Person.LASTNAME).nullsLast());
        TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
            @Override
            public Void execute( final Connection c) throws SQLException {
                final SelectResult res = executeQuery(c, query);
                assertResultSet(res, RESULT_NULLS_LAST, "test-order-nulls-last");
                return null;
            }
        });
    }

    @Test
    public void testOrderAggregate() {
        final SqlCommand<SelectStatement> query = SQL.select(Person.GENDER, Sql.max(Person.HEIGTH))
                .from(Person.TABLE)
                .groupBy(Person.GENDER)
                .orderBy(Sql.desc(Sql.max(Person.HEIGTH)));
        TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
            @Override
            public Void execute( final Connection c) throws SQLException {
                final SelectResult res = executeQuery(c, query);
                assertResultSet(res, RESULT_AGGREGATE_ORDER, "test-order-aggregate");
                return null;
            }
        });
    }

    @Test
    public void testOrderMultiple() {
        final SqlCommand<SelectStatement> query = SQL.select(Person.NAME, Person.HEIGTH)
                .from(Person.TABLE).orderBy(Person.HEIGTH, Sql.desc(Person.NAME));
        TestDb.INSTANCE.executeSingle( new DbOperation<Void>() {
            @Override
            public Void execute( final Connection c) throws SQLException {
                final SelectResult res = executeQuery(c, query);
                assertResultSet(res, RESULT_ORDER_MULTIPLE, "test-order-multiple");
                return null;
            }
        });
    }

}
