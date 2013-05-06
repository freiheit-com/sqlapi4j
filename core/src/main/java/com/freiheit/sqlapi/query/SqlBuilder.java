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

import com.freiheit.sqlapi.meta.SequenceDef;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.clause.FromClause;
import com.freiheit.sqlapi.query.clause.SubFromClause;

// TODO: https://github.com/greenhornet/freiheit_sqlapi/issues/16
//join
//Sequence-Handling
//select ohne where-clause erlauben
@ParametersAreNonnullByDefault
public interface SqlBuilder {

    /**
     * Start a SELECT statement.
     *
     * @param items the expressions to be selected
     */
    @Nonnull
	FromClause select( SelectListItem<?>... items);

	/**
	 * Start a subselect query.
	 *
	 * @param item the expression to be selected
	 */
    @Nonnull
	<T> SubFromClause<T> subSelect( SelectListItem<T> item);

	/**
	 * Start an INSERT statement.
	 *
	 * @param table the target table for the INSERT
	 */
    @Nonnull
	InsertCommand insert( TableDef table);

	/**
	 * Start an UPDATE statement.
	 *
	 * @param table the target table for the UPDATE
	 */
    @Nonnull
	UpdateCommand update( TableDef table);

	/**
	 * Start a DELETE statement.
	 *
	 * @param table the target table for the DELETE
	 */
    @Nonnull
	DeleteCommand delete( TableDef table);

	/**
	 * Start a CREATE TABLE statement.
	 *
	 * @param table the table definition
	 */
    @Nonnull
	CreateTableCommand createTable( TableDef table);

	/**
	 * Return a command to select the next value of the specified sequence.
	 *
	 * @param sequence the sequence definition
	 */
    @Nonnull
	SelectSequenceCommand nextval( SequenceDef sequence);

}
