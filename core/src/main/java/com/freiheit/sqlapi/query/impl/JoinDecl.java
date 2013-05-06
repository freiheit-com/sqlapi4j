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
package com.freiheit.sqlapi.query.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.freiheit.sqlapi.meta.AbstractColumnDef;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.FromDef;

public class JoinDecl implements FromDef, OnPart {

    private final JoinType _joinType;
	private final TableDef _table1;
	private AbstractColumnDef<?> _column1;
	private final TableDef _table2;
	private AbstractColumnDef<?> _column2;

	private JoinDecl(@Nonnull final JoinType joinType,
	                 @Nonnull final TableDef table1,
	                 @Nullable final AbstractColumnDef<?> column1,
	                 @Nullable final TableDef table2,
	                 @Nullable final AbstractColumnDef<?> column2) {
		_joinType= joinType;
		_table1= table1;
		_column1= column1;
		_table2= table2;
		_column2= column2;
	}

    @Nonnull
    public static OnPart makeLeftOuterJoin(@Nonnull final TableDef left, @Nonnull final TableDef right) {
        if (left.equals(right)) {
            throw new IllegalArgumentException("self-joins are not yet supported and will result in runtime errors.");
        }
        return new JoinDecl(JoinType.LEFT_OUTER_JOIN, left, null, right, null);
    }

    @Override
    @Nonnull
    public FromDef on(final AbstractColumnDef<?> left, final AbstractColumnDef<?> right) {
        _column1 = left;
        _column2 = right;
        return this;
    }

	@Nonnull
	public JoinType getJoinType() {
		return _joinType;
	}

	@Nonnull
    public TableDef getTable1() {
        return _table1;
    }

	@Nullable
    public AbstractColumnDef<?> getColumn1() {
        return _column1;
    }

	@Nullable
    public TableDef getTable2() {
        return _table2;
    }

	@Nullable
    public AbstractColumnDef<?> getColumn2() {
        return _column2;
    }

    @Override
	public String getTableName() {
		String typeString;

		//FIXME (CM): https://github.com/greenhornet/freiheit_sqlapi/issues/10
		// Put into dialect?
		switch (_joinType){
		case LEFT_OUTER_JOIN: typeString = " LEFT OUTER JOIN "; break;
		case RIGHT_OUTER_JOIN: typeString = " RIGHT OUTER JOIN "; break;
		case INNER_JOIN: typeString = " INNER JOIN "; break;
		case FULL_OUTER_JOIN: typeString = " FULL OUTER JOIN "; break;
		default: throw new UnsupportedOperationException();
		}


        return _table1.getTableName() + typeString + _table2.getTableName() +
                " ON " + _column1.fqName() + " = " + _column2.fqName();
	}
}
