/**
 * Copyright 2014 freiheit.com technologies gmbh
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

import static com.freiheit.sqlapi4j.query.Sql.Ordering;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Representing a single expression for the ORDER BY clause
 *
 * @author Ulrich Geilmann (ulrich.geilmann@freiheit.com)
 */
public interface OrderItem {

    public static enum Direction {
        ASC,
        DESC
    }

    public <T> T accept(@Nonnull OrderItemVisitor<T> visitor);

    @ParametersAreNonnullByDefault
    public interface OrderItemVisitor<T> {

        T visit(SelectListItem<?> item);

        T visit(Ordering<?> ordering);

    }

}
