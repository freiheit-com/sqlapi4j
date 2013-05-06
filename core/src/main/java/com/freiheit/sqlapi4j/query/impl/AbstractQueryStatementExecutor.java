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
package com.freiheit.sqlapi4j.query.impl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.generate.SqlQueryType;

@ParametersAreNonnullByDefault
abstract class AbstractQueryStatementExecutor<T, R> extends AbstractSqlStatementExecutor<T, R> {

    @Nonnull private final SqlQueryType _sqlQueryType;

    protected AbstractQueryStatementExecutor(final AbstractQueryStatementExecutor<?, ?> part) {
        this(part.getSqlQueryType(), part.getDialect(), part.getGenerator());
    }

    protected AbstractQueryStatementExecutor(final SqlQueryType queryType, final SqlDialect dialect, final SqlGenerator generator) {
        super(dialect, generator);
        _sqlQueryType= queryType;
    }

    @Nonnull
    protected SqlQueryType getSqlQueryType() {
        return _sqlQueryType;
    }

}
