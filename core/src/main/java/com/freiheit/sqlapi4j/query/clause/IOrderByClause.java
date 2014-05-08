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

import com.freiheit.sqlapi4j.query.OrderItem;
import com.freiheit.sqlapi4j.query.SqlCommand;


@ParametersAreNonnullByDefault
public interface IOrderByClause<R, T extends SqlCommand<R>, C extends ILimitClause<R, T, ? extends IOffsetClause<R, T>>> {

    /**
     * Specify the result ordering for the ORDER BY clause.
     */
    @Nonnull
    C orderBy(OrderItem... items);

}
