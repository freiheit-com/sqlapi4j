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
package com.freiheit.sqlapi4j.meta;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.Column2ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;
import com.freiheit.sqlapi4j.query.SelectListItem;

// TODO: https://github.com/greenhornet/freiheit_sqlapi/issues/15
//Single-Column custom types
/**
 * Metadata for nullable columns of database tables.
 *
 * param <T> the column's data type
 *
 * @author Jörg Kirchhof (joerg@freiheit.com) (initial creation)
 */
@ParametersAreNonnullByDefault
public class ColumnDefNullable<T> extends AbstractColumnDef<T> implements SelectListItem<T> {

	/**
	 * ctor.
	 */
	protected ColumnDefNullable(final String dbColname, final DbType<T> dbType) {
		super(dbColname, dbType);
	}

	/**
	 * Create a new column defination.
	 *
	 * @param dbColname the column name
	 * @param dbType the column type
	 */
    public static <T> ColumnDefNullable<T> of(final String dbColname, final DbType<T> dbType) {
		return new ColumnDefNullable<T>( dbColname, dbType);
	}

    /**
     * Yields a column assignment setting this column to the given value.
     */
	public ColumnValueAssignment<T> set(@Nullable final T value) {
	    return super.setInternal(value);
	}

    /**
     * Yields a column assignment setting this column to the value of the specified other column.
     */
	public Column2ColumnAssignment<T> set(final ColumnDefNullable<T> other) {
	    return super.setInternal(other);
	}
}
