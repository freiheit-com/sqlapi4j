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
public class NumberColumnConverter<T extends Number,TDB extends Number> implements ColumnConverter<T,TDB> {

	public enum NumberType {
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,

		;
	}

	private final int _sqlType;
	@Nonnull private final String _sqlTypeDecl;
	@Nonnull private final NumberType _numType;

	public NumberColumnConverter( final int sqlType, final NumberType numType, final String sqlTypeDecl) {
		_sqlType= sqlType;
		_numType= numType;
		_sqlTypeDecl= sqlTypeDecl;
	}

	@Override
	public int getSqlType() {
		return _sqlType;
	}

	@Override
	@SuppressWarnings( "unchecked")
	public T fromDb( final TDB value, final DbType<T> clazz) {
		if( value == null) {
            return null;
        }
		switch( _numType) {
		case BYTE:
			return (T)Byte.valueOf( value.byteValue());
		case SHORT:
			return (T)Short.valueOf( value.shortValue());
		case INT:
			return (T)Integer.valueOf( value.intValue());
		case LONG:
			return (T)Long.valueOf( value.longValue());
		case FLOAT:
			return (T)Float.valueOf( value.floatValue());
		case DOUBLE:
			return (T)Double.valueOf( value.doubleValue());
		}
		return (T)value;
	}

	@Override
	@SuppressWarnings( "unchecked")
	public TDB toDb( final T value, final DbType<T> clazz) {
		return (TDB)value;
	}

	@Override
	public String getSqlTypeDeclaration( final DbType<T> dbType) {
		return _sqlTypeDecl;
	}

}
