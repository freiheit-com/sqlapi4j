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

import java.sql.Types;
import java.util.Calendar;

import com.freiheit.sqlapi.generate.SqlStdConverter;
import com.freiheit.sqlapi.meta.ColumnConverter;
import com.freiheit.sqlapi.meta.DbEnumType;
import com.freiheit.sqlapi.meta.impl.CalendarColumnConverter;
import com.freiheit.sqlapi.meta.impl.DefaultColumnConverter;
import com.freiheit.sqlapi.meta.impl.NumberColumnConverter;

public class PsqlStdConverter implements SqlStdConverter {

	private static final String STRING_SQL_TYPE= "VARCHAR";
	private static final String INT_SQL_TYPE= "INT";
	private static final String TIMESTAMP_SQL_TYPE= "TIMESTAMP";
	private static final String LONG_SQL_TYPE= "BIGINT";
	private static final String BOOLEAN_SQL_TYPE= "BOOLEAN";
	private static final String FLOAT_SQL_TYPE = "FLOAT";

	@Override
	public ColumnConverter<Boolean,Boolean> getBooleanConverter() {
		return new DefaultColumnConverter<Boolean>( Types.BOOLEAN, BOOLEAN_SQL_TYPE);
	}

	@Override
	public ColumnConverter<Long,?> getLongConverter() {
		return new NumberColumnConverter<Long,Number>( Types.INTEGER, NumberColumnConverter.NumberType.LONG, LONG_SQL_TYPE);
	}

	@Override
	public ColumnConverter<Calendar,?> getDateTimeConverter() {
		return new CalendarColumnConverter( Types.DATE, TIMESTAMP_SQL_TYPE);
	}

	@Override
	public ColumnConverter<Calendar,?> getTimestampConverter() {
		return new CalendarColumnConverter( Types.TIMESTAMP, TIMESTAMP_SQL_TYPE);
	}

	@Override
	public ColumnConverter<Integer,Number> getIntConverter() {
		return new NumberColumnConverter<Integer,Number>( Types.INTEGER, NumberColumnConverter.NumberType.INT, INT_SQL_TYPE);
	}

	@Override
	public ColumnConverter<String,String> getStringConverter() {
		return new DefaultStringColumnConverter( Types.VARCHAR, STRING_SQL_TYPE);
	}

	@Override
	public <E extends Enum<E>> ColumnConverter<E, String> getEnumConverter() {
		return new DbEnumType.DbEnumTypeConverter<E>( STRING_SQL_TYPE + "(255)");
	}

	public static String appendSqlStrLen( String sqlStrType, int strLen) {
		return sqlStrType + "(" + strLen + ")";
	}

	@Override
	public ColumnConverter<Float,?> getFloatConverter() {
		return new NumberColumnConverter<Float,Number>( Types.FLOAT, NumberColumnConverter.NumberType.FLOAT, FLOAT_SQL_TYPE);
	}

}
