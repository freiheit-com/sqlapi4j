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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.query.SqlExecutor;
import com.freiheit.sqlapi.query.statements.InsertStatement;
import com.freiheit.sqlapi.query.statements.UpdateStatement;

@ParametersAreNonnullByDefault
public final class EnhancingExecutor extends DelegatingExecutor {

    private final List<StatementEnhancer> _enhancements;

    public EnhancingExecutor(final SqlExecutor executor, final List<StatementEnhancer> enhancements) {
        super(executor);
        _enhancements = enhancements;
    }

    private final InsertStatement enhance(InsertStatement stmt) {
        for (final StatementEnhancer m: _enhancements) {
            stmt = m.enhance(stmt);
        }
        return stmt;
    }

    private final UpdateStatement enhance(UpdateStatement stmt) {
        for (final StatementEnhancer m: _enhancements) {
            stmt = m.enhance(stmt);
        }
        return stmt;
    }

    @Override
    public int execute(@Nonnull final Connection connection, @Nonnull final InsertStatement statement) throws SQLException {
        return super.execute(connection, enhance(statement));
    }

    @Override
    public String render(@Nonnull final InsertStatement statement) throws SQLException {
        return super.render(enhance(statement));
    }

    @Override
    public int execute(@Nonnull final Connection connection, @Nonnull final UpdateStatement statement) throws SQLException {
        return super.execute(connection, enhance(statement));
    }

    @Override
    public String render(@Nonnull final UpdateStatement statement) {
        return super.render(enhance(statement));
    }
}
