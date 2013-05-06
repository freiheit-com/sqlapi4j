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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.generate.SqlStdConverter;
import com.freiheit.sqlapi.meta.ColumnConverter;
import com.freiheit.sqlapi.meta.DbEnumType;
import com.freiheit.sqlapi.meta.DbType;

@ParametersAreNonnullByDefault
public class ConverterRegistry {

	@SuppressWarnings("rawtypes")
    @Nonnull private final Map<Class<? extends DbType>,ColumnConverter<?, ?>> _converterMap = new HashMap<Class<? extends DbType>,ColumnConverter<?, ?>>();
	@CheckForNull private final ConverterRegistry _parent;

	public ConverterRegistry(@Nullable final ConverterRegistry parent) {
	    super();
		_parent= parent;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void setDefaults(final SqlStdConverter stdConv) {

		_converterMap.put( DbType.DbBoolean.class, stdConv.getBooleanConverter());
		_converterMap.put( DbType.DbInteger.class, stdConv.getIntConverter());
		_converterMap.put( DbType.DbLong.class, stdConv.getLongConverter());
		_converterMap.put( DbType.DbString.class, stdConv.getStringConverter());
		_converterMap.put( DbType.DbDateTime.class, stdConv.getDateTimeConverter());
		_converterMap.put( DbType.DbTimestamp.class, stdConv.getTimestampConverter());
		_converterMap.put( DbEnumType.class, stdConv.<Enum>getEnumConverter());
		_converterMap.put(DbType.DbFloat.class, stdConv.getFloatConverter());
	}

	@CheckForNull
	public <T> ColumnConverter<T,?> getConverter(@Nullable final Class<? extends DbType<T>> cls) {
		@SuppressWarnings("unchecked")
        ColumnConverter<T,?> conv= (ColumnConverter<T,?>)_converterMap.get( cls);
		if( conv == null && _parent != null) {
            return _parent.getConverter( cls);
        }
		return conv;
	}

	@Nonnegative
	public <T> ConverterRegistry registerConverter(final Class<? extends DbType<? extends T>> type, final ColumnConverter<? extends T,?> conv) {
		_converterMap.put( type, conv);
		return this;
	}
}
