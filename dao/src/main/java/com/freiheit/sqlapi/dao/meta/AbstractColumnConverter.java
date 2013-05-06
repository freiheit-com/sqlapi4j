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
package com.freiheit.sqlapi.dao.meta;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.meta.ColumnConverter;
import com.freiheit.sqlapi.meta.DbType;

/**
 * Abstract class for all column converters in our application.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 * @param <T> the java type
 * @param <DB> the db type
 */
@ParametersAreNonnullByDefault
public abstract class AbstractColumnConverter<T, DB> implements ColumnConverter<T, DB>{

    @Nonnull private final SqlType _type;

    protected AbstractColumnConverter(final SqlType type) {
        super();
        _type = type;
    }

    @Override
    public int getSqlType() {
        return _type.getSqlType();
    }

    @Override
    public String getSqlTypeDeclaration(final DbType<T> arg0) {
        return _type.getSqlName();
    }

}
