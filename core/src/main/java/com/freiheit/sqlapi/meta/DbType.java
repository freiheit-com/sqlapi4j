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
package com.freiheit.sqlapi.meta;

import java.util.Calendar;

import javax.annotation.Nonnegative;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for all database type mappings.
 */
@ParametersAreNonnullByDefault
public interface DbType<T> {

    /**
     * {@link DbType} for integer columns (like INT).
     */
	static class DbInteger implements DbType<Integer> {
	}

	/**
	 * {@link DbType} for long integer columns (like BIGINT).
	 */
	static class DbLong implements DbType<Long> {
	}

	/**
	 * {@link DbType} for string columns (like VARCHAR).
	 */
	static class DbString implements DbType<String> {

		private final int _strLen;

		public DbString(final int strLen) {
			_strLen= strLen;
		}

		@Nonnegative
		public int getStrLen() {
			return _strLen;
		}

	}

	/**
	 * {@link DbType} for boolean columns (like BOOLEAN).
	 */
	static class DbBoolean implements DbType<Boolean> {
	}

	/**
	 * DbType for date columns (like DATE).
	 */
	static class DbDateTime implements DbType<Calendar> {
	}

	/**
	 * DbType for timestamp columns (like TIMESTAMP).
	 */
	static class DbTimestamp implements DbType<Calendar> {
	}

	/**
	 * DbType for float columns (like FLOAT).
	 */
	static class DbFloat implements DbType<Float> {
	}
}
