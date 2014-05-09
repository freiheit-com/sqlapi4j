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
package com.freiheit.sqlapi4j.query.statements;


import javax.annotation.CheckForNull;

import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.OrderItem;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.clause.LockMode;

/**
 * Data of a query statement.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public interface SelectStatement {

    @CheckForNull
	SelectListItem<?>[] getSelectItems();

    @CheckForNull
	FromDef[] getFromDef();

    @CheckForNull
	BooleanExpression getWhere();

    @CheckForNull
	OrderItem[] getOrderItems();

    @CheckForNull
	SelectListItem<?>[] getGroupItems();

    @CheckForNull
	Integer getLimitNum();

    @CheckForNull
	Integer getOffsetNum();

    @CheckForNull
    LockMode getLockMode();

    @CheckForNull
    BooleanExpression getHaving();

    @CheckForNull
    String getIndexName();
}
