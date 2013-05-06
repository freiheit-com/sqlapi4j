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
package com.freiheit.sqlapi.generate.impl;

import com.freiheit.sqlapi.meta.ColumnConverter;
import com.freiheit.sqlapi.meta.DbType;

public class DefaultStringColumnConverter implements ColumnConverter<String,String> {

	private final int _sqlType;
	private final String _sqlTypeDecl;

	public DefaultStringColumnConverter( int sqlType, String sqlTypeDecl) {
		_sqlType= sqlType;
		_sqlTypeDecl= sqlTypeDecl;
	}

	@Override
	public int getSqlType() {
		return _sqlType;
	}

	@Override
	public String fromDb( String value, DbType<String> clazz) {
		return value;
	}

	@Override
	public String toDb( String value, DbType<String> clazz) {
		return value;
	}

	@Override
	public String getSqlTypeDeclaration( DbType<String> dbType) {
		int strLen= ((DbType.DbString)dbType).getStrLen();
		return PsqlStdConverter.appendSqlStrLen( _sqlTypeDecl, strLen);
	}

}
