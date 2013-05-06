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
package com.freiheit.sqlapi.query.impl;

import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.SelectListItem;

public abstract class ColumnComparison<V> implements BooleanExpression {
	private final Object _value;
	private final SelectListItem<V> _col;
	private final ValueComparisonType _comparison;

	public ColumnComparison( ValueComparisonType comparison, final SelectListItem<V> col, final V value) {
		_comparison= comparison;
		_col= col;
		_value= value;
	}

	public ValueComparisonType getComparison() {
		return _comparison;
	}

	public Object getValue() {
		return _value;
	}

	public SelectListItem<V> getCol() {
		return _col;
	}
}
