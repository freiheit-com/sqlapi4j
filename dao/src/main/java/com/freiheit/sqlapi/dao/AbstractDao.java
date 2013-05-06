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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freiheit.sqlapi.dao.IntegrationCallbacks.Delete;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.Find;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.FindFirst;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.FindUnique;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.GetUnique;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.Insert;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.InsertGenerateIds;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.SelectSequenceValue;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.SqlIntegrationCallback;
import com.freiheit.sqlapi.dao.IntegrationCallbacks.Update;
import com.freiheit.sqlapi.domain.exception.DataAccessException;
import com.freiheit.sqlapi.domain.exception.EntityNotUniqueException;
import com.freiheit.sqlapi.domain.exception.UnexpectedEntityChangeException;
import com.freiheit.sqlapi.domain.type.IntegrationCallback;
import com.freiheit.sqlapi.domain.type.TransactionTemplate;
import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.meta.TableDef;
import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.DeleteCommand;
import com.freiheit.sqlapi.query.InsertCommand;
import com.freiheit.sqlapi.query.SelectCommand;
import com.freiheit.sqlapi.query.SelectListItem;
import com.freiheit.sqlapi.query.SelectSequenceCommand;
import com.freiheit.sqlapi.query.Sql;
import com.freiheit.sqlapi.query.SqlBuilder;
import com.freiheit.sqlapi.query.SqlExecutor;
import com.freiheit.sqlapi.query.SqlResultRow;
import com.freiheit.sqlapi.query.UpdateCommand;
import com.freiheit.sqlapi.query.clause.LimitClause;
import com.freiheit.sqlapi.query.clause.LockMode;
import com.freiheit.sqlapi.query.clause.WhereClause;
import com.freiheit.sqlapi.query.impl.SqlBuilderImpl;

/**
 * An abstract parent to daos significantly easing the use of the sqlapi.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
@ParametersAreNonnullByDefault
public abstract class AbstractDao {

    private static final Logger       LOGGER = LoggerFactory.getLogger(AbstractDao.class);

    private final TransactionTemplate _integrationExecutor;
    private final SqlBuilder          _sql;
    private final SqlExecutor         _executor;

    private static final class SelectListItemConverter<T> implements ResultTransformer<T> {

        @Nonnull private final SelectListItem<T> _col;

        public SelectListItemConverter(final SelectListItem<T> col) {
            super();
            _col = col;
        }

        @Override
        public T apply(final SqlResultRow input) {
            final T ret = input.get(_col);
            if (ret == null) {
                throw new IllegalStateException("Result row converted to null: " + input.toString());
            }
            return ret;
        }
    }

    private static final class SqlIntegrationCallbackBridge<T> implements IntegrationCallback<T> {

        private final SqlIntegrationCallback<T> _cb;
        private final SqlExecutor _sqlExecutor;

        public SqlIntegrationCallbackBridge(final SqlIntegrationCallback<T> cb, final SqlExecutor sqlExecutor) {
            _cb = cb;
            _sqlExecutor = sqlExecutor;
        }

        @Override
        public T execute(final Connection connection) throws SQLException {
            return _cb.execute(_sqlExecutor, connection);
        }
    }

    protected AbstractDao(
            @Nonnull
            final TransactionTemplate integrationExecutor,
            @Nonnull
            final SqlExecutor sqlexecutor
    ) {
        this(integrationExecutor, sqlexecutor, new SqlBuilderImpl());
    }

    protected AbstractDao(
            @Nonnull
            final TransactionTemplate integrationExecutor,
            @Nullable
            final SqlExecutor sqlExecutor,
            @Nonnull
            final SqlBuilder sqlBuilder
    ) {
        super();
        _integrationExecutor = integrationExecutor;
        _sql = sqlBuilder;
        _executor = sqlExecutor;
    }

    /**
     * Return a sql builder for simple usages.
     *
     * @return the sql builder
     */
    @Nonnull
    protected SqlBuilder sql() {
        return _sql;
    }

    /**
     * Delegate the execution to the bound {@link TransactionTemplate}.
     */
    @Nonnull
    protected <T> T execute(final SqlIntegrationCallback<T> exec) throws DataAccessException {
        return _integrationExecutor.execute(new SqlIntegrationCallbackBridge<T>(exec, _executor));
    }

    /**
     * Get all values of the given Table by the id, converting the result with the converter function.
     */
    @Nonnull
    protected <T> List<T> findAll(final ResultTransformer<T> converter, final TableDef table, final BooleanExpression... expr) {
        return execute(new Find<T>(converter, _sql.select(table.getColumns()).from(table).where(expr)));
    }

    /**
     * Get all values of the given Table with the given expression, offset and limit, converting the result with the converter function.
     */
    @Nonnull
    protected <T> List<T> findWithLimitAndOffset(
        final ResultTransformer<T> converter,
        final TableDef table,
        @Nonnegative final int offset,
        @Nonnegative final int limit,
        final BooleanExpression... expr
    ) {
        final SelectCommand command = _sql.select(table.getColumns()).from(table).where(expr).limit(limit).offset(offset);
        return execute(new Find<T>(converter, command));
    }

    /**
     * Get all values of the given Table with the given expression, offset and limit, converting the result with the converter function.
     */
    @Nonnull
    protected <T> List<T> findWithLimitAndOffset(
        final ResultTransformer<T> converter,
        final TableDef table,
        @Nonnegative final int offset,
        @Nonnegative final int limit,
        @Nonnull final  LockMode lockMode,
        final BooleanExpression... expr
    ) {
        final SelectCommand command = _sql.select(table.getColumns()).from(table).where(expr).limit(limit).offset(offset).withLockMode(lockMode);
        return execute(new Find<T>(converter, command));
    }

    /**
     * Get all values of the given Table with the given expression, offset and limit, converting the result with the converter function.
     */
    @Nonnull
    protected <T> List<T> findWithLimitAndOffset(
        final ResultTransformer<T> converter,
        final TableDef table,
        final SelectListItem<?> order,
        @Nonnegative final int offset,
        @Nonnegative final int limit,
        final BooleanExpression... expr
    ) {
        final SelectCommand command = _sql.select(table.getColumns()).from(table).where(expr).orderBy(order).limit(limit).offset(offset);
        return execute(new Find<T>(converter, command));
    }

    /**
     * Get the number of matching rows for the given expression.
     */
    @Nonnegative
    protected <I> int count(final ColumnDef<I> id, final TableDef table, final BooleanExpression... expr) {
        final SelectListItem<Long> count = Sql.count(id);
        return getUnique(count, sql().select(count).from(table).where(expr)).intValue();
    }

    /**
     * Get all values of the given Table by the id, converting the result with the converter function.
     */
    @Nonnull
    protected <T> T getUnique(final ResultTransformer<T> converter, final TableDef table, final BooleanExpression... expr) {
        return execute(new GetUnique<T>(converter, _sql.select(table.getColumns()).from(table).where(expr)));
    }

    /**
     * Common usecase: Create a select statement for all items of the given table.
     */
    @Nonnull
    protected WhereClause selectAllColumnsOf(final TableDef table) {
        return sql().select(table.getColumns()).from(table);
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @Nonnull
    protected <T> List<T> findAll(final ResultTransformer<T> converter, final SelectCommand query) {
        return execute(new Find<T>(converter, query));
    }

    /**
     * execute the query and return the value of the given column.
     */
    @Nonnull
    protected <T> List<T> findAll(@Nonnull final SelectListItem<T> col, @Nonnull final SelectCommand query) {
        return execute(new Find<T>(new SelectListItemConverter<T>(col), query));
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @CheckForNull
    protected <T> T findUnique(final ResultTransformer<T> converter, final LimitClause query) {
        return execute(new FindUnique<T>(converter, query));
    }

    /**
     * Get all values of the given Table by the id, converting the result with the converter function.
     */
    @CheckForNull
    protected <T> T findUnique(final ResultTransformer<T> converter, final TableDef table, final BooleanExpression... expr) {
        return execute(new FindUnique<T>(converter, _sql.select(table.getColumns()).from(table).where(expr)));
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @CheckForNull
    protected <T> T findFirst(final ResultTransformer<T> converter, final LimitClause query) {
        return execute(new FindFirst<T>(converter, query));
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @Nonnegative
    protected int update(final UpdateCommand query) {
        return execute(new Update(query)).intValue();
    }

    /**
     * Execute the update command and make sure it only has a singe result.
     */
    protected void updateUnique(final UpdateCommand query) {
        final int count = update(query);
        if (count == 0) {
            LOGGER.warn("Could not execute update " + query + ".");
            throw new UnexpectedEntityChangeException();
        }
        if (count > 1) {
            LOGGER.error("unique update got " + count + " results: " + query + ".");
            throw new EntityNotUniqueException();
        }
    }

    /**
     * Execute the delete command and make sure it only has a single result.
     */
    protected void deleteUnique(final DeleteCommand query) {
        final int count = delete(query);
        if (count == 0) {
            LOGGER.warn("Could not execute delete statement " + query + ".");
            throw new UnexpectedEntityChangeException();
        }
        if (count > 1) {
            LOGGER.error("unique delete got " + count + " results: " + query + ".");
            throw new EntityNotUniqueException();
        }
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @Nonnegative
    protected int delete(final DeleteCommand query) {
        return execute(new Delete(query)).intValue();
    }


    /**
     * execute the query and convert the result with the given converter.
     */
    @Nonnull
    protected <T> T getUnique(final ResultTransformer<T> converter, final LimitClause query) {
        return execute(new GetUnique<T>(converter, query));
    }

    /**
     * execute the query and return the value of the given column.
     */
    @CheckForNull
    protected <T> T findUnique(final SelectListItem<T> col, final LimitClause query) {
        return execute(new FindUnique<T>(new SelectListItemConverter<T>(col), query));
    }

    /**
     * execute the query and return the value of the given column.
     */
    @Nonnull
    protected <T> T getUnique(final SelectListItem<T> col, final LimitClause query) {
        return execute(new GetUnique<T>(new SelectListItemConverter<T>(col), query));
    }

    /**
     * Execute the Insert command, and insert the id from the sequence to the specified id column.
     *
     * <b>Note:</b> The type of idCol must have a converter to/from Long.
     *
     * @throws IllegalArgumentException if the insert command contains multiple rows
     */
    protected <I> I insertGenerateId(final ColumnDef<I> idCol, final SelectSequenceCommand sequenceCmd, final InsertCommand cmd) {
        final List<I> r = insertGenerateIds(idCol, sequenceCmd, cmd);
        if (r.size() != 1) {
            throw new IllegalArgumentException("Insert produced " + r.size() + " results, expected " + 1);
        }
        return r.get(0);
    }

    /**
     * Execute the Insert command, and insert the ids from the sequence to the specified id column, supports insert commands
     * with multiple rows.
     *
     * The returned Ids will be in the same order as the calls to InsertCommand.values().
     *
     * <b>Note:</b> The type of idCol must have a converter to/from Long.
     */
    @Nonnull
    protected <I> List<I> insertGenerateIds(final ColumnDef<I> idCol, final SelectSequenceCommand sequenceCmd, final InsertCommand cmd) {
        return execute(new InsertGenerateIds<I>(idCol, cmd, sequenceCmd));
    }

    /**
     * Execute the Insert command, and insert the id to the specified id column.
     */
    protected <I> void insert(final InsertCommand cmd) {
        execute(new Insert(cmd));
    }

    /**
     * Creates a callback which returns the next sequence value
     */
    @Nonnull
    protected Long select(final SelectSequenceCommand sequence) {
        return execute(new SelectSequenceValue(sequence));
    }


}
