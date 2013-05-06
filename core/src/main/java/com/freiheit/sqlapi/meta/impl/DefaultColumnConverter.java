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
package com.freiheit.sqlapi.meta.impl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.meta.ColumnConverter;
import com.freiheit.sqlapi.meta.DbType;

@ParametersAreNonnullByDefault
public class DefaultColumnConverter<T> implements ColumnConverter<T,T> {

	private final int _sqlType;
	@Nonnull private final String _sqlTypeDecl;

	public DefaultColumnConverter( int sqlType, String sqlTypeDecl) {
		_sqlType= sqlType;
		_sqlTypeDecl= sqlTypeDecl;
	}

	@Override
	public int getSqlType() {
		return _sqlType;
	}

	@Override
	public T fromDb( T value, DbType<T> clazz) {
		return value;
	}

	@Override
	public T toDb( T value, DbType<T> clazz) {
		return value;
	}

	@Override
	public String getSqlTypeDeclaration( DbType<T> dbType) {
		return _sqlTypeDecl;
	}

}
