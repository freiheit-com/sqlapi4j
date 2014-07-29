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
package com.freiheit.sqlapi4j.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freiheit.sqlapi4j.domain.exception.EntityNotFoundException;
import com.freiheit.sqlapi4j.domain.exception.EntityNotUniqueException;
import com.freiheit.sqlapi4j.domain.type.TransactionTemplate;
import com.freiheit.sqlapi4j.meta.ColumnDef;
import com.freiheit.sqlapi4j.meta.SequenceDef;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;
import com.freiheit.sqlapi4j.query.InsertCommand;
import com.freiheit.sqlapi4j.query.SelectCommand;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.SqlBuilder;
import com.freiheit.sqlapi4j.query.SqlExecutor;
import com.freiheit.sqlapi4j.query.clause.LimitClause;
import com.freiheit.sqlapi4j.query.clause.LockMode;
import com.freiheit.sqlapi4j.query.clause.WhereClause;
import com.freiheit.sqlapi4j.query.impl.SqlBuilderImpl;

/**
 * Abstract baseclass for Daos that handle a single table for a standard BL-Layer object.
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 * @param <I> the Id type
 * @param <T> the BL value type
 */
@ParametersAreNonnullByDefault
public abstract class SingleTableDao<I, T> extends AbstractDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleTableDao.class);

    @Nonnull private final TableDef _table;
    @Nonnull private final ColumnDef<I> _pkColumn;
    @Nonnull private final ResultTransformer<T> _reader;

    public SingleTableDao(
            final TransactionTemplate integrationExecutor,
            final SqlExecutor sqlExecutor,
            final SqlBuilder sql,
            final TableDef table,
            final ColumnDef<I> pkColumn,
            final ResultTransformer<T> reader
    ) {
        super(integrationExecutor, sqlExecutor, sql);
        _table = table;
        _pkColumn = pkColumn;
        _reader = reader;
    }

    public SingleTableDao(
            final TransactionTemplate integrationExecutor,
            final SqlExecutor sqlExecutor,
            final TableDef table,
            final ColumnDef<I> pkColumn,
            final ResultTransformer<T> reader
    ) {
        this(integrationExecutor, sqlExecutor, new SqlBuilderImpl(), table, pkColumn, reader);
    }

    /**
     * The table definition of this dao.
     *
     * @return The table definition of this dao.
     */
    @Nonnull
    protected TableDef table() {
        return _table;
    }

    /**
     * The primary key column of this dao.
     *
     * @return The primary key column of this dao.
     */
    @Nonnull
    protected ColumnDef<I> pkColumn() {
        return _pkColumn;
    }

    /**
     * The standard converter of this dao.
     *
     * @return The standard converter of this dao.
     */
    @Nonnull
    protected ResultTransformer<T> reader() {
        return _reader;
    }

    /**
     * Insert an entity, setting the given column values.
     *
     * @return the id of the inserted entity.
     */
    @Nonnull
    protected I insert(final SequenceDef sequence, final ColumnValueAssignment<?>... cols) {
        return insertGenerateId(pkColumn(), sql().nextval(sequence), sql().insert(table()).values(cols));
    }

    /**
     * Insert an entity, setting the given column values.
     *
     * Only use this method, if the pkColumn is an auto-increment-column.
     *
     * @return the id of the inserted entity.
     */
    @Nonnull
    protected I insert(final ColumnValueAssignment<?>... cols) {
        return insertGenerateId(pkColumn(), sql().insert(table()).values(cols));
    }

    /**
     * Insert multiple entities, setting the given column values.
     *
     * Example Usage:
     * <pre>
     *  insert(
     *      ImmutableList.of(
     *          ImmutableList.of(NAME.set("Hans"), SURNAME.set("Klein")),
     *          ImmutableList.of(NAME.set("Frank"), SURNAME.set("Dieters"))
     *      )
     *  );
     * </pre>
     * @return the ids of the inserted entities in the same order as the input list.
     * @throws IllegalArgumentException if the items in the list do not have the same column at corresponding positions.
     */
    @Nonnull
    protected List<I> insert(final SequenceDef sequence, final List<? extends List<? extends ColumnValueAssignment<?>>> cols) throws IllegalArgumentException {
        InsertCommand insert = sql().insert(table());
        for (final List<? extends ColumnValueAssignment<?>> row : cols) {
            insert = insert.values(row.toArray(new ColumnValueAssignment<?>[row.size()]));
        }
        return insertGenerateIds(pkColumn(), sql().nextval(sequence), insert);
    }


    /**
     * Execute the given find-query and convert the result with the standard converter of this dao.
     *
     * @param query the query to execute
     * @return the converted values
     */
    @Nonnull
    protected List<T> findAll(final SelectCommand query) {
        return super.findAll(reader(), query);
    }

    /**
     * Execute the query and convert the result with the given converter.
     */
    @CheckForNull
    protected T findFirst(final LimitClause query) {
        return super.findFirst(reader(), query);
    }


    /**
     * Execute the query and convert the result with the given converter.
     */
    @CheckForNull
    protected T findUnique(final LimitClause query) {
        return super.findUnique(reader(), query);
    }

    /**
     * Get all values of the given Table by the id, converting the result with the converter function.
     */
    @Nonnull
    protected List<T> findAll(final BooleanExpression... expr) {
        return super.findAll(reader(), table(), expr);
    }

    /**
     * Like {@link #findAll(BooleanExpression...)}, but checks that the result exactly has the expected size.
     */
    @Nonnull
    protected List<T> getAll(@Nonnegative final int exactNumberOfResults, final BooleanExpression... expr) {
        return getAll(reader(), exactNumberOfResults, expr);
    }

    /**
     * Like {@link #getAll(int, BooleanExpression...)}, but uses the specified reader.
     */
    @Nonnull
    protected <T2> List<T2> getAll(
        final ResultTransformer<T2> reader,
        @Nonnegative final int exactNumberOfResults,
        final BooleanExpression... expr
    ) {
        final List<T2> result = super.findAll(reader, table(), expr);
        final int size = result.size();
        if (size < exactNumberOfResults) {
            LOGGER.error("EntityNotFound?: Expected " + exactNumberOfResults + " for " + Arrays.toString(expr) + ", but got "
                + size + " results [" + this + "]: " + result);
            throw new EntityNotFoundException();
        }
        if (size > exactNumberOfResults) {
            LOGGER.error("EnitityNotUnique?: Expected " + exactNumberOfResults + " for " + Arrays.toString(expr) + ", but got "
                + size + " results [" + this + "]: " + result);
            throw new EntityNotUniqueException();
        }
        return result;
    }

    /**
     * Get all values of the given Table for the given expression with offset and limit, converting
     * the result with the converter function.
     */
    @Nonnull
    protected List<T> findWithOffsetAndLimit(@Nonnegative final int offset, @Nonnegative final int limit, final BooleanExpression... expr) {
        return super.findWithLimitAndOffset(reader(), table(), offset, limit, expr);
    }

    /**
     * Get all values of the given Table for the given expression with offset and limit, converting
     * the result with the converter function.
     */
    @Nonnull
    protected List<T> findWithOffsetAndLimit(
        @Nonnegative final int offset,
        @Nonnegative final int limit,
        final SelectListItem<?> order,
        final BooleanExpression... expr
    ) {
        return super.findWithLimitAndOffset(reader(), table(), order, offset, limit, expr);
    }

    /**
     * Get all values of the given Table for the given expression with offset and limit, converting
     * the result with the converter function.
     */
    @Nonnull
    protected List<T> findWithOffsetAndLimit(@Nonnegative final int offset, @Nonnegative final int limit,@Nonnull final LockMode lockMode, final BooleanExpression... expr) {
        return super.findWithLimitAndOffset(reader(), table(), offset, limit, lockMode, expr);
    }

    /**
     * Get the number of matching rows for the given expression.
     */
    @Nonnegative
    protected int count(final BooleanExpression... expr) {
        return count(pkColumn(), table(), expr);
    }

    /**
     * Returns a list of all values of the given select list item (for example: a column)
     * for a select on the table of this dao with the given expression as the where clause.
     */
    @Nonnull
    protected <S> List<S> findAll(final SelectListItem<S> item, final BooleanExpression... expr) {
        return findAll(item, sql().select(item).from(table()).where(expr));
    }


    /**
     * Returns a list of all values of the given select list item (for example: a column)
     * for a select on the table of this dao with the given expression as the where clause.
     */
    @Nonnull
    protected <S> S getUnique(final SelectListItem<S> item, final BooleanExpression... expr) {
        return getUnique(item, sql().select(item).from(table()).where(expr));
    }

    /**
     * Find the single result - if there are no matches, null will be returned.
     * It is considered an error if there are more than one matches
     */
    @CheckForNull
    protected T findUnique(final BooleanExpression... expr) {
        return super.findUnique(reader(), table(), expr);
    }

    /**
     * execute the query and convert the result with the given converter.
     */
    @Nonnull
    protected T getUnique(final LimitClause query) {
        return super.getUnique(reader(), query);
    }

    /**
     * Get all values of the given Table by the id, converting the result with the converter function.
     */
    @Nonnull
    protected T getUnique(final BooleanExpression... expr) {
        return super.getUnique(reader(), table(), expr);
    }

    /**
     * Get the model object by querying the Id.
     *
     * @return the Model Object
     */
    @Nonnull
    protected T getById(final I id) {
        return getUnique(_reader, _table, _pkColumn.eq(id));
    }

    /**
     * Get the model objects by querying the Ids.
     *
     * @param ids
     *            the ids to query for - this is a set,
     *            because due to the get semantics we will check that the number of queried entities
     *            corresponds to the number of returned entities.
     * @return the Model Object
     */
    @Nonnull
    protected Collection<T> getByIds(final Set<I> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return getAll(ids.size(), _pkColumn.in(ids));
    }

    /**
     * Select all from the table
     */
    @Nonnull
    protected WhereClause selectAllColumns() {
        return sql().select(table().getColumns()).from(table());
    }

    /**
     * Updates an entity with the given id with its assignments.
     */
    protected void updateUnique(final I id, final ColumnAssignment<?>... assignments) {
        updateUnique(sql().update(_table).values(assignments).where(_pkColumn.eq(id)));
    }

    /**
     * Updates all entities with the value matching the expression.
     */
    protected void updateFieldAll(final ColumnAssignment<?> assignment, final BooleanExpression... expr) {
        update(sql().update(_table).values(assignment).where(expr));
    }

    /**
     * Delete an entity by id.
     */
    protected void deleteById(final I id) {
        deleteUnique(pkColumn().eq(id));
    }

    /**
     * Delete an entity by id.
     */
    protected void deleteUnique(final BooleanExpression... expr) {
        deleteUnique(sql().delete(table()).where(expr));
    }

    /**
     * deletes all according to the boolean expression.
     */
    protected int deleteAll(final BooleanExpression...  expr){
        return delete(sql().delete(table()).where(expr));
    }

}
