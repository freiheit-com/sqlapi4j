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

import java.sql.SQLException;

import javax.annotation.Nonnull;

/**
 * The result of an SQL query.
 */
public interface SelectResult {

    /**
     * Get the number of columns (fields) in the result.
     */
	int getNofColumns() throws SQLException;

	/**
	 * Get the column definitions
	 */
	@Nonnull
	Iterable<String> columnDefs() throws SQLException;

    /**
     * Returns <tt>true</tt> if the result has more rows.
     */
	boolean hasNext() throws SQLException;

	/**
	 * Get the next row of the result.
	 *
	 * {@link #next()} should only be called if {@link #hasNext()} returned <code>true</code>.
	 */
	@Nonnull
	SqlResultRow next() throws SQLException;

	/**
	 * Closes the underlying ResultSet.
	 */
	void close() throws SQLException;
}
