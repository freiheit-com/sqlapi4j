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
package com.freiheit.sqlapi.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.generate.impl.H2DbDialect;
import com.freiheit.sqlapi.query.SqlBuilder;
import com.freiheit.sqlapi.query.SqlExecutor;
import com.freiheit.sqlapi.query.impl.SqlBuilderImpl;
import com.freiheit.sqlapi.query.impl.SqlExecutorImpl;
import com.freiheit.sqlapi.test.hsql.TestBase;

@ParametersAreNonnullByDefault
public class DaoTestBase extends TestBase {

    protected static final SqlExecutor EXEC= new SqlExecutorImpl( new H2DbDialect( new DaoTestConverterRegistry(null)));
    protected static final SqlBuilder SQL= new SqlBuilderImpl();

    @Nonnull
    protected static <R, T extends R> List<List<R>> toNestedList(final T[][] nestedArray) {
        final List<List<R>> result = new ArrayList<List<R>>(nestedArray.length);
        for (final T[] array : nestedArray) {
            result.add(Arrays.<R>asList(array));
        }
        return result;
    }

    @Nonnull
    protected static <R, T extends R> List<R> toList(final T[] array) {
        return Arrays.<R>asList(array);
    }

}
