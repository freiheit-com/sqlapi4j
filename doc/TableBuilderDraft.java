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
package com.freiheit.sqlapi.meta.impl;


import javax.annotation.Nonnull;

import com.freiheit.hoi.business.goal.model.GoalId;
import com.freiheit.sqlapi.dao.meta.PKColumnDef;
import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.meta.TableDef;

/**
 * TableDef for the goal-table
 * @author "Christian Ewers (christian.ewers@freiheit.com)"
 *
 */
public final class FooTable {
    private static final class TableDefBuilder {
        private final String _tableName;
        TableDefBuilder(final String name) {
            _tableName = name;
        }
        public TableDef build() {
            throw new UnsupportedOperationException();
        }
    }
    private static abstract class AbstractTable {
         protected final TableDefBuilder b;
         private final TableDef _def = b.build();

         protected AbstractTable(final String name, final String aliasName) {
             b = new TableDefBuilder(name);
         }

        @Override
        public TableDef table() {
             return _def;
         }
    }

    public static final class GoalTable extends AbstractTable {
        public static final GoalTable INSTANCE = new GoalTable(null);

        private final ColumnDef<String> _title = b.varchar("title", 80);
        private final PKColumnDef<GoalId> _id = b.custom(new PKColumnDef<GoalId>("id", GoalId.class, "goal_id_seq"));


        private GoalTable(@Nullable final String aliasName) {
            super("name", aliasName);
        }

        public ColumnDef<String> title() {
            return _title;
        }
        public static GoalTable alias(@Nonnull final String aliasName) {
            return new GoalTable(aliasName);
        }

    }
}

// select count(g1.id) as count
//        from goal g1, goal g2
//        where g1.cloned_from_id = g2.id and
//              g1.freeze_state = 'NOT_FROZEN' and
//              g2.state = 'ACHIEVED';


//        final GoalTable table1  = GoalTable.as("table1");
//        final GoalTable table2  = GoalTable.as("table2");
//        final SelectListItem<Long> count = Sql.count(table1.ID);
//        sql().select(count)
//             .from(table1.TABLE.leftOuterJoin(table2.TABLE).on(table1.ID, table2.CLONED_FROM_ID))
//             .where(table2.STATE.eq(GoalState.ACHIEVED));
//
//        FooTable.GoalTable t1 = FooTable.GoalTable.alias("t1");
//        FooTable.GoalTable t2 = FooTable.GoalTable.alias("t2");
//        final SelectCommand query = sql().select(t2.title())
//                .from(t1.TABLE, t2.TABLE)
//                .where(GoalTable.FREEZE_STATE.of(t2).eq(FreezeState.NOT_FROZEN));
//
//
//        final Alias table1 = Sql().makeAlias(GoalTable);
//        final Alias table2 = Sql().makeAlias(GoalTable);
//        final SelectListItem<Long> count = Sql.count(GoalTable.ID.from(table1));
//        sql().select(count)
//             .from(table1.TABLE.selfJoin().on(GoalTable.ID, GoalTable.CLONED_FROM_ID))
//             .where(GoalTable.STATE.from(table2).eq(GoalState.ACHIEVED));
//
//
//        final SelectListItem<Long> count = Sql.count(GoalTable.named("table1").ID);
//        sql().select(count)
//             .from(GoalTable.named("table1").TABLE.selfJoin().on(GoalTable.ID, GoalTable.CLONED_FROM_ID))
//             .where(GoalTable.named("table2").STATE.eq(GoalState.ACHIEVED));
//
//
//        final SelectListItem<Long> count = Sql.count(GoalTable.ID.alias("table1"));
//        sql().select(count)
//             .from(GoalTable.named("table1").TABLE.selfJoin().on(GoalTable.ID, GoalTable.CLONED_FROM_ID))
//             .where(GoalTable.named("table2").STATE.eq(GoalState.ACHIEVED));
//
//        final SelectListItem<Long> count = Sql.count(GoalTable.ID.alias("table1"));
//        sql().select(count)
//             .from(GoalTable.named("table1").TABLE.selfJoin().on(GoalTable.ID, GoalTable.CLONED_FROM_ID))
//             .where(GoalTable.named("table2").STATE.eq(GoalState.ACHIEVED));
//
//        final TableDef t1 = GoalTable.TABLE;
//        final AliasedTableDef t2 = GoalTable.TABLE.alias("t2");
//        final SelectCommand query = sql().select(GoalTable.CLONED_FROM_ID.of(t2))
//                                          .from(t1, t2)
//                                          .where(GoalTable.FREEZE_STATE.of(t2).eq(FreezeState.NOT_FROZEN));
