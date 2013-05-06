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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Converter between database types and Java classes.
 *
 * @param <T> the Java type
 * @param <DB> the database type
 */
@ParametersAreNonnullByDefault
public interface ColumnConverter<T,DB> {

    /**
     * Convert the specified Java value with the specified mapper to a value which can be stored in the database column.
     */
    @CheckForNull
	DB toDb(@Nullable T value, DbType<T> clazz);

	/**
	 * Convert the specified database value with the specified mapper to a Java value.
	 */
    @CheckForNull
	T fromDb(@Nullable DB value, DbType<T> dbType);

	/**
	 * @see java.sql.Types
	 */
	int getSqlType();

	/**
	 * Get the SQL column type declaration as string.
	 */
	@Nonnull
	String getSqlTypeDeclaration(DbType<T> dbType);

}
