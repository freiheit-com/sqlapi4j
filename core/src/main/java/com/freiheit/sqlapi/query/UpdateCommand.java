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
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.query.statements.UpdateStatement;


/**
 * Update rows in the database.
 */
@ParametersAreNonnullByDefault
public interface UpdateCommand extends SqlCommand<UpdateStatement> {

    /**
     * Specify the column assignments for the update.
     */
    @Nonnull
	UpdateCommand values( ColumnAssignment<?>... assignments);

	/**
	 * Constrain the UPDATE to rows matching the specified conditions.
	 */
    @Nonnull
	UpdateCommand where( BooleanExpression... booleanCombinations);

}
