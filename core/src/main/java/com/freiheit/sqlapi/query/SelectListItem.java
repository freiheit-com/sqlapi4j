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
package com.freiheit.sqlapi.query;

import javax.annotation.Nonnull;

import com.freiheit.sqlapi.meta.DbType;

// TODO: https://github.com/greenhornet/freiheit_sqlapi/issues/17
// spezielle SelectListItem-Wrapper, denen man einen Property-Accessor
// geben kann, über den die Zuweisung der Spalte aus dem Resultset stattfindet
public interface SelectListItem<T> {
	//	@SuppressWarnings( "unchecked")
	//	static final SelectListItem<?> ALL= new SelectListItem() {
	//		@Override
	//		public String name() {
	//			return "*";
	//		}
	//
	//		@Override
	//		public String fqName() {
	//			return "*";
	//		}
	//
	//		@Override
	//		public boolean isColumnName() {
	//			return false;
	//		}
	//
	//		@Override
	//		public DbType type() {
	//			return null;
	//		}
	//
	//	};

    /**
     * Get the item's name.
     *
     * In case of database columns, this is the column name.
     */
    @Nonnull
	String name();

	/**
	 * Get the item's fully qualified name.
	 *
	 * In case of database columns, this is the fully qualified column name (including the table name).
	 */
    @Nonnull
	String fqName();

	// FIXME (JK): https://github.com/greenhornet/freiheit_sqlapi/issues/12
    //Flag bei der Generierung benutzen
	// false -> Dialekt benutzen
	/**
	 * Whether this item references a database column by name.
	 */
	boolean isColumnName();

	/**
	 * Get the item's type.
	 *
	 * In case of database columns, this is the column type.
	 */
	@Nonnull
	DbType<T> type();

}
