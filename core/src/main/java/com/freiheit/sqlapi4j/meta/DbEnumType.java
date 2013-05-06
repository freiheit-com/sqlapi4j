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
package com.freiheit.sqlapi4j.meta;

import java.sql.Types;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


/**
 * This class is not part of the public API. Do not use it unless you know exactly what you are doing.
 */
@ParametersAreNonnullByDefault
public class DbEnumType<E extends Enum<E>> implements DbType<E> {

	public static class DbEnumTypeConverter<E extends Enum<E>> implements ColumnConverter<E,String> {

		@Nonnull private final String _sqlTypeDecl;

		public DbEnumTypeConverter(final String sqlTypeDecl) {
			_sqlTypeDecl= sqlTypeDecl;
		}

		@Override
		public int getSqlType() {
			return Types.VARCHAR;
		}

		@Override
		public E fromDb( String value, DbType<E> dbType) {
			return value == null ? null : Enum.valueOf( ((DbEnumType<E>)dbType).getTypeClass(), value);
		}

		@Override
		public String toDb( E value, DbType<E> dbType) {
			return value == null ? null : value.name();
		}

		@Override
		public String getSqlTypeDeclaration( DbType<E> dbType) {
			return _sqlTypeDecl;
		}

	}

	@Nonnull private final Class<E> _clazz;

	public DbEnumType(final Class<E> instance) {
		_clazz= instance;
	}

	@Nonnull
	public Class<E> getTypeClass() {
		return _clazz;
	}
}
