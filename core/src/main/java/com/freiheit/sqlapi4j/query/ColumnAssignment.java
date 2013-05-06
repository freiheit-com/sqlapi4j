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

import javax.annotation.Nonnull;

import com.freiheit.sqlapi4j.meta.AbstractColumnDef;

/**
 * Assignment of a column
 *
 * @param <T> the type of the target column
 */
public interface ColumnAssignment<T> {

	public static enum Type {

	    /**
	     * Assign a value to the column.
	     */
		VALUE_ASSIGNMENT,

		/**
		 * Assign the value of another column to this column.
		 */
		COLUMN_ASSIGNMENT,

		;

	}

	/**
	 * Get the target column.
	 */
	@Nonnull
	AbstractColumnDef<T> getLhsColumn();

	@Nonnull
	Type getAssignmentType();

}
