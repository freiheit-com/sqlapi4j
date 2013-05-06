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

import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.Sql.And;
import com.freiheit.sqlapi4j.query.Sql.ColumnComparisonOperation;
import com.freiheit.sqlapi4j.query.Sql.ColumnEquals;
import com.freiheit.sqlapi4j.query.Sql.ColumnEqualsIgnoreCase;
import com.freiheit.sqlapi4j.query.Sql.ColumnGreaterThan;
import com.freiheit.sqlapi4j.query.Sql.ColumnLessThan;
import com.freiheit.sqlapi4j.query.Sql.InSubselectExpression;
import com.freiheit.sqlapi4j.query.Sql.InValuesExpression;
import com.freiheit.sqlapi4j.query.Sql.IsNotNull;
import com.freiheit.sqlapi4j.query.Sql.IsNull;
import com.freiheit.sqlapi4j.query.Sql.Not;
import com.freiheit.sqlapi4j.query.Sql.Or;
import com.freiheit.sqlapi4j.query.Sql.PairInExpression;
import com.freiheit.sqlapi4j.query.impl.Column2ColumnComparison;

@ParametersAreNonnullByDefault
public interface BooleanExpressionVisitor<T> {

	public T visit( IsNull isNull);

	public T visit( IsNotNull isNotNull);

	public T visit( ColumnEquals<?> eq);

	public T visit( ColumnEqualsIgnoreCase<?> ieq);

	public T visit( ColumnComparisonOperation<?> comp);

	public T visit( Column2ColumnComparison<?> eq);

	public T visit( Not not);

	public T visit( ColumnLessThan<?> lt);

	public T visit( ColumnGreaterThan<?> gt);

	public T visit( And and);

	public T visit( Or or);

	public T visit( InValuesExpression<?> expr);

	public T visit( InSubselectExpression<?> expr);
	
	public T visit ( PairInExpression<?, ?> expr);

}
