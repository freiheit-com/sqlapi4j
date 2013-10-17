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
package com.freiheit.sqlapi4j.dao.meta;

import java.sql.Types;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Enum of the DB Types used by tango custom sql converters.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 */
@ParametersAreNonnullByDefault
public enum SqlTypes implements SqlType {

    /**
     * VARCHAR. Strings of varying length.
     */
    VARCHAR (Types.VARCHAR, "VARCHAR"),

    /**
     * INTEGER. Integral values.
     */
    INTEGER(Types.INTEGER, "INT"),

    /**
     * DOUBLE. Floating point values.
     */
    DOUBLE(Types.DOUBLE, "double precision"),

    /**
     * TIMESTAMP. Date and time values.
     */
    TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP"),

    /**
     * BIGINT. Integral values with a larger range than {@link SqlTypes#INTEGER}.
     */
    BIGINT(Types.BIGINT, "BIGINT"),

    /**
     * SMALLINT. Integral values with a smaller range than {@link SqlTypes#INTEGER} mostly precsion 5.
     */
    SMALLINT(Types.SMALLINT, "SMALLINT"),

    /**
     * BOOLEAN. Boolean values.
     */
    BOOLEAN(Types.BOOLEAN, "BOOLEAN"),

    ;

    @Nonnull private final String _sqlName;
    @Nonnull private final int _sqlType;

    private SqlTypes(final int sqlType, final String sqlName) {
        _sqlName = sqlName;
        _sqlType = sqlType;
    }

    @Override
    public String getSqlName() {
        return _sqlName;
    }

    @Override
    public int getSqlType() {
        return _sqlType;
    }

}
