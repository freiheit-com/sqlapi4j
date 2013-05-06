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
package com.freiheit.sqlapi4j.query.clause;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.SqlCommand;

@ParametersAreNonnullByDefault
public interface IFromClause<R, T extends SqlCommand<R>, C extends IWhereClause<R, T, ? extends IGroupByClause<R, T, ?>>> extends SqlCommand<R> {

    /**
     * Specify the tables for the FROM clause.
     */
    @Nonnull
    C from(FromDef... fromDef);

}
