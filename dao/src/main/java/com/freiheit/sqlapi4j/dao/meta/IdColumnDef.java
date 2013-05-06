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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.domain.type.Id;
import com.freiheit.sqlapi4j.meta.ColumnDef;

/**
 * Primary key column definition.
 *
 * @param <T> the id type
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 */
@ParametersAreNonnullByDefault
public final class IdColumnDef<T extends Id> extends ColumnDef<T> {

    /**
     * @param name the column name
     * @param cls the column type
     * @param sequenceName the name of the sequence used to generate the values
     */
    public IdColumnDef(final String name, final Class<T> cls) {
        super(name, new IdDbType<T>(cls));
    }


    @Override
    public boolean equals(@Nullable final Object obj) {
        // only overridden to make findbugs happy
        // ATTENTION: if we would change the implementation,
        // we would break symmetry! Meaning we would risk: o1.equals(o2) != o2.equals(o1)
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
