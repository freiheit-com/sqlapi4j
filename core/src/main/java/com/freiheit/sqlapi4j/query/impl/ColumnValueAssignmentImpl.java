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
package com.freiheit.sqlapi4j.query.impl;

import com.freiheit.sqlapi4j.meta.AbstractColumnDef;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;

public class ColumnValueAssignmentImpl<T> implements ColumnValueAssignment<T> {
	private final AbstractColumnDef<T> _col;
	private final T _value;

	public ColumnValueAssignmentImpl( AbstractColumnDef<T> col, T value) {
		_col= col;
		_value= value;
	}

	@Override
	public AbstractColumnDef<T> getLhsColumn() {
		return _col;
	}

	@Override
	public T getValue() {
		return _value;
	}

	@Override
	public ColumnAssignment.Type getAssignmentType() {
		return Type.VALUE_ASSIGNMENT;
	}

}
