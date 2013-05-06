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

import static com.freiheit.sqlapi.dao.StatementModifierUtil.prependToArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freiheit.sqlapi.domain.exception.EntityNotFoundException;
import com.freiheit.sqlapi.domain.exception.EntityNotUniqueException;
import com.freiheit.sqlapi.meta.ColumnDef;
import com.freiheit.sqlapi.query.ColumnAssignment;
import com.freiheit.sqlapi.query.DeleteCommand;
import com.freiheit.sqlapi.query.InsertCommand;
import com.freiheit.sqlapi.query.SelectCommand;
import com.freiheit.sqlapi.query.SelectResult;
import com.freiheit.sqlapi.query.SelectSequenceCommand;
import com.freiheit.sqlapi.query.SqlExecutor;
import com.freiheit.sqlapi.query.SqlResultRow;
import com.freiheit.sqlapi.query.UpdateCommand;
import com.freiheit.sqlapi.query.impl.InsertStatementImpl;
import com.freiheit.sqlapi.query.impl.SqlAst;
import com.freiheit.sqlapi.query.statements.InsertStatement;


/**
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 *
 */
@ParametersAreNonnullByDefault
final class IntegrationCallbacks {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationCallbacks.class);

    private IntegrationCallbacks() {
        super();
    }

    /**
     * Integration callback.
     *
     * @author Klas Kalass (klas.kalass@freiheit.com)
     * @param <T> the return type of the callback
     */
    public interface SqlIntegrationCallback<T> {

        /**
         * Execute on the given connection, using the provided sql builder object
         * for building your sql.
         */
        T execute(SqlExecutor executor, Connection connection) throws SQLException;

    }

    /**
     * Next sequence value.
     *
     * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
     */
    static final class SelectSequenceValue implements SqlIntegrationCallback<Long> {

        @Nonnull private final SelectSequenceCommand _sequenceQuery;

        protected SelectSequenceValue(final SelectSequenceCommand sequenceStatement) {
            super();
            _sequenceQuery = sequenceStatement;
        }

        @Override
        public Long execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            return Long.valueOf(executor.execute(connection, _sequenceQuery.stmt()));
        }
    }

    static final class InsertGenerateIds<I> implements SqlIntegrationCallback<List<I>> {

        private final ColumnDef<I> _idCol;
        private final InsertCommand _insertCommand;
        private final SelectSequenceCommand _sequenceCommand;

        protected InsertGenerateIds(
            final ColumnDef<I> idCol,
            final InsertCommand insertCommand,
            final SelectSequenceCommand sequenceCommand
        ) {
            _idCol = idCol;
            _insertCommand = insertCommand;
            _sequenceCommand = sequenceCommand;
        }

        protected InsertStatement addIds(final InsertStatement statement, final List<I> ids) {
            final List<ColumnAssignment<?>[]> rows = statement.getRows();
            if (rows.size() != ids.size()) {
                throw new IllegalArgumentException("Got " + ids.size() + " Ids for " + rows.size() + " rows.");
            }
            final List<ColumnAssignment<?>[]> rowsWithIds = new ArrayList<ColumnAssignment<?>[]>();
            for (int i = 0; i < rows.size(); i++) {
                final ColumnAssignment<?>[] row = rows.get(i);
                final I id = ids.get(i);
                final ColumnAssignment<?>[] assignments = prependToArray(_idCol.set(id), row);
                rowsWithIds.add(assignments);
            }
            return new InsertStatementImpl(statement.getTable(), statement.getWhere(), statement.getFromDefs(), rowsWithIds);
        }

        @Override
        public List<I> execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            final InsertStatement stmt = _insertCommand.stmt();

            final List<ColumnAssignment<?>[]> rows = stmt.getRows();
            final List<I> ids = new ArrayList<I>(rows.size());
            for (int i = 0; i < rows.size(); i++) {
                final I id = executor.execute(connection, _sequenceCommand.stmt(), _idCol);
                ids.add(id);
            }

            final int rowcount = executor.execute(connection, addIds(stmt, ids));
            // a rowcount != 1 is highly unlikely here, but better check:
            if (rowcount == 0) {
                LOGGER.warn("Failed to insert single row [" + this + "]: " + executor.render(stmt));
                throw new EntityNotFoundException();
            }
            return ids;
        }
    }

    static final class Delete implements SqlIntegrationCallback<Integer> {

        private final DeleteCommand _command;

        protected Delete(final DeleteCommand command) {
            super();
            _command = command;
        }

        @Override
        public final Integer execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            return executor.execute(connection, _command.stmt());
        }
    }

    static final class Update implements SqlIntegrationCallback<Integer> {

        private final UpdateCommand _command;

        protected Update(final UpdateCommand command) {
            super();
            _command = command;
        }

        @Override
        public final Integer execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            return executor.execute(connection, _command.stmt());
        }
    }

    static final class Insert implements SqlIntegrationCallback<Void> {

        private final InsertCommand _command;

        protected Insert(final InsertCommand command) {
            super();
            _command = command;
        }

        @Override
        public final Void execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            executor.execute(connection, _command.stmt());
            return null;
        }
    }

    /**
     * Abstract baseclass for queries on single objects.
     * "Find" means that the result may or may not exist. But it is expected to be unique, if it does.
     * If two results are found, we will throw and exception.
     *
     * @author Klas Kalass (klas.kalass@freiheit.com)
     * @param <T> the data type
     */
     static final class FindUnique<T> implements SqlIntegrationCallback<T> {

        @Nonnull private final ResultTransformer<T> _converter;
        @Nonnull private final SelectCommand _command;

        protected FindUnique(final ResultTransformer<T> converter, final SelectCommand command) {
            super();
            _converter = converter;
            _command = command;
        }

        @Override
        public final T execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            // copy the statement - do not alter the original one.
            final SqlAst ast = new SqlAst(_command.stmt());
            ast.setLimitNum(2);
            final SelectResult result = executor.execute(connection, ast);
            try {
                if (!result.hasNext()) {
                    return null;
                }

                final SqlResultRow row = result.next();

                if (result.hasNext()) {
                    // HU? There's more than one entry for this userId?
                    // throw something!
                    LOGGER.warn("Found more than one entry  [" + this + "]: " + executor.render(ast));
                    throw new EntityNotUniqueException();
                }

                return _converter.apply(row);

            } finally {
                result.close();
            }
        }
    }

    /**
     * Abstract baseclass for queries.
     *
     * @author Klas Kalass (klas.kalass@freiheit.com)
     * @param <T> the data type
     */
    static final class Find<T> implements SqlIntegrationCallback<List<T>> {

        @Nonnull private final ResultTransformer<T> _converter;
        @Nonnull private final SelectCommand _command;

        protected Find(final ResultTransformer<T> converter, final SelectCommand command) {
            super();
            _converter = converter;
            _command = command;
        }

        @Override
        public final List<T> execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            final SelectResult result = executor.execute(connection, _command.stmt());
            try {
                final List<T> list = new ArrayList<T>();
                while (result.hasNext()) {
                    final T item = _converter.apply(result.next());
                    list.add(item);
                }
                return Collections.unmodifiableList(list);

            } finally {
                result.close();
            }
        }
    }

    static final class FindFirst<T> implements SqlIntegrationCallback<T> {

        @Nonnull private final ResultTransformer<T> _converter;
        @Nonnull private final SelectCommand _command;

        protected FindFirst(final ResultTransformer<T> converter, final SelectCommand command) {
            super();
            _converter = converter;
            _command = command;
        }


        @Override
        public final T execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            final SqlAst ast = new SqlAst(_command.stmt());
            ast.setLimitNum(1);
            final SelectResult result = executor.execute(connection, ast);
            try {
                if (!result.hasNext()) {
                    return null;
                }
                return _converter.apply(result.next());

            } finally {
                result.close();
            }
        }
    }

    /**
     * Abstract baseclass for queries on single objects that are expected to exist.
     * <p>
     * "Get" refers to the fact that the instance is expected to exist. An exception will be thrown if the queried Object does not exist.
     * </p>
     *
     * @author Klas Kalass (klas.kalass@freiheit.com)
     * @param <T> the data type
     */
    static final class GetUnique<T> implements SqlIntegrationCallback<T> {

        private final ResultTransformer<T> _converter;
        private final SelectCommand _command;

        protected GetUnique(final ResultTransformer<T> converter, final SelectCommand command) {
            super();
            _converter = converter;
            _command = command;
        }

        @Override
        public final T execute(final SqlExecutor executor, final Connection connection) throws SQLException {
            // copy the statement - do not alter the original one.
            final SqlAst ast = new SqlAst(_command.stmt());
            ast.setLimitNum(2);
            final SelectResult result = executor.execute(connection, ast);
            try {
                if (!result.hasNext()) {
                    LOGGER.warn("Could not find any entry  [" + this + "]: " + executor.render(ast));
                    throw new EntityNotFoundException();
                }

                final SqlResultRow row = result.next();

                if (result.hasNext()) {
                    // HU? There's more than one entry for this userId?
                    // throw something!
                    LOGGER.warn("Found more than one entry  [" + this + "]: " + executor.render(ast));
                    throw new EntityNotUniqueException();
                }
                return _converter.apply(row);

            } finally {
                result.close();
            }
        }
    }

}
