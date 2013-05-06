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

import com.freiheit.sqlapi.domain.type.Id;
import com.freiheit.sqlapi.meta.AbstractColumnDef;
import com.freiheit.sqlapi.meta.DbType;

/**
 * To be used as parameter to {@link AbstractColumnDef} to signify the usage of {@link Id}.
 *
 * @param <T> der konkrete Java-Typ der Id.
 * @author Andreas Baldeau (andreas.baldeau@freiheit.com) (initial creation)
 */
@ParametersAreNonnullByDefault
public class IdDbType<T extends Id> implements DbType<T> {

    @Nonnull private final Class<T> _cls;

    public IdDbType(final Class<T> cls) {
        _cls = cls;
    }

    @Nonnull
    public Class<T> getTypeClass() {
        return _cls;
    }

}
