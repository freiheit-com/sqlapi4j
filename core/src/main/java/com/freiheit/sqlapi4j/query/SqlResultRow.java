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
package com.freiheit.sqlapi4j.query;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


/**
 * A single row in the result set of a query.
 */
@ParametersAreNonnullByDefault
public interface SqlResultRow {

    /**
     * Get the specified column (field) of the result row. The result may be null.
     *
     * @param <T> the field's type
     */
    @CheckForNull
    <T, S extends SelectListItem<T>> T get(S item);

    /**
     * Get the specified column (field) of the result row. If the value is <code>null</code>, an exception is thrown
     *
     * @param <T> the field's type
     */
    @Nonnull
    <T, S extends NonnullSelectListItem<T>> T get(S item);

	/**
	 * Get all columns (fields) of the result row.
	 */
    @Nonnull
	Iterable<?> columns();

}
