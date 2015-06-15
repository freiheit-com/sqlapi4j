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
package com.freiheit.sqlapi4j.meta.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.DbType;

@ParametersAreNonnullByDefault
public class CalendarColumnConverter implements ColumnConverter<Calendar,java.util.Date> {

	private final int _sqlType;
	@Nonnull private final String _sqlTypename;

	public CalendarColumnConverter( int sqlType, String sqlTypename) {
		_sqlType= sqlType;
		_sqlTypename= sqlTypename;
	}

	@Override
	public Calendar fromDb( java.util.Date value, DbType<Calendar> dbType) {
	    if (value == null) {
	        return null;
	    }
	    
		Calendar res= Calendar.getInstance();
		res.setTimeInMillis( value.getTime());
		return res;
	}

	@Override
	public int getSqlType() {
		return _sqlType;
	}

	@Override
	public java.util.Date toDb( Calendar value, DbType<Calendar> dbType) {
	    
	    if (value == null) {
	        return null;
	    }
	    
		switch( _sqlType) {
		case Types.TIMESTAMP:
			return new Timestamp( value.getTimeInMillis());
		default:
			return new Date( value.getTimeInMillis());
		}
	}

	@Override
	public String getSqlTypeDeclaration( DbType<Calendar> dbType) {
		return _sqlTypename;
	}

}
