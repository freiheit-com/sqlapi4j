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

import java.util.Calendar;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
public final class ColumnDefs {

    /**
     * Integer column.
     *
     * @param name the column name
     */
    @Nonnull
	public static ColumnDefNullable<Integer> integerNullable(final String name) {
		return ColumnDefNullable.of( name, new DbType.DbInteger());
	}

    /**
     * Integer column.
     *
     * @param name the column name
     */
    @Nonnull
    public static ColumnDef<Integer> integer(final String name) {
        return ColumnDef.of( name, new DbType.DbInteger());
    }

	/**
	 * Long integer column.
	 *
	 * @param name the column name
	 */
    @Nonnull
	public static ColumnDefNullable<Long> longNullable(final String name) {
		return ColumnDefNullable.of( name, new DbType.DbLong());
	}

    /**
     * Long integer column.
     *
     * @param name the column name
     */
    @Nonnull
    public static ColumnDef<Long> longT(final String name) {
        return ColumnDef.of( name, new DbType.DbLong());
    }

	/**
	 * String column with a maximum width of 255 characters.
	 *
	 * @param name the column name
	 * @see #varcharNullable(String, int)
	 */
    @Nonnull
	public static ColumnDefNullable<String> varcharNullable(final String name) {
	    return varcharNullable(name, 255);
	}

    /**
     * String column with a maximum width of 255 characters.
     *
     * @param name the column name
     * @see #varchar(String, int)
     */
    @Nonnull
    public static ColumnDef<String> varchar(final String name) {
        return varchar(name, 255);
    }

	/**
	 * String column with specified maximum width.
	 *
	 * @param name the column name
	 * @param strLen the maximum width in characters
	 */
    @Nonnull
	public static ColumnDefNullable<String> varcharNullable(final String name, @Nonnegative final int strLen) {
		return ColumnDefNullable.of( name, new DbType.DbString( strLen));
	}

    /**
     * String column with specified maximum width.
     *
     * @param name the column name
     * @param strLen the maximum width in characters
     */
    @Nonnull
    public static ColumnDef<String> varchar(final String name, @Nonnegative final int strLen) {
        return ColumnDef.of( name, new DbType.DbString( strLen));
    }

	/**
	 * Boolean column.
	 *
	 * @param name the column name
	 */
    @Nonnull
	public static ColumnDefNullable<Boolean> boolNullable(final String name) {
		return ColumnDefNullable.of( name, new DbType.DbBoolean());
	}

    /**
     * Boolean column.
     *
     * @param name the column name
     */
    @Nonnull
    public static ColumnDef<Boolean> bool(final String name) {
        return ColumnDef.of( name, new DbType.DbBoolean());
    }

	/**
	 * Date column.
	 *
	 * @param name the column name
	 */
    @Nonnull
	public static ColumnDefNullable<Calendar> dateTimeNullable(final String name) {
		return ColumnDefNullable.of( name, new DbType.DbDateTime());
	}

    /**
     * Date column.
     *
     * @param name the column name
     */
    @Nonnull
    public static ColumnDef<Calendar> dateTime(final String name) {
        return ColumnDef.of( name, new DbType.DbDateTime());
    }

	/**
	 * Timestamp column.
	 *
	 * @param name the column name
	 */
    @Nonnull
	public static ColumnDefNullable<Calendar> timestampNullable(final String name) {
		return ColumnDefNullable.of( name, new DbType.DbTimestamp());
	}

    /**
     * Timestamp column.
     *
     * @param name the column name
     */
    @Nonnull
    public static ColumnDef<Calendar> timestamp(final String name) {
        return ColumnDef.of( name, new DbType.DbTimestamp());
    }

	/**
	 * Enum column. Enum values are stored as {@link Enum#name()} in string columns.
	 *
	 * @param name the column name
	 * @param enumClass the enum {@link Class}
	 */
    @Nonnull
	public static <T extends Enum<T>> ColumnDefNullable<T> enumTypeNullable(final String name, final Class<T> enumClass) {
		return ColumnDefNullable.of( name, new DbEnumType<T>( enumClass));
	}

    /**
     * Enum column. Enum values are stored as {@link Enum#name()} in string columns.
     *
     * @param name the column name
     * @param enumClass the enum {@link Class}
     */
    @Nonnull
    public static <T extends Enum<T>> ColumnDef<T> enumType(final String name, final Class<T> enumClass) {
        return ColumnDef.of( name, new DbEnumType<T>( enumClass));
    }

}
