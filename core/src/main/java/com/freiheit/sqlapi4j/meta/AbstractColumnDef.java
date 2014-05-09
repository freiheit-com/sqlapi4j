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
package com.freiheit.sqlapi4j.meta;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.Column2ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.SubQuery;

// TODO: Single-Column custom types [see https://github.com/greenhornet/freiheit_sqlapi/issues/3]
/**
 * Metadata for columns of database tables.
 *
 * param <T> the column's data type
 *
 * @author Jörg Kirchhof (joerg@freiheit.com) (initial creation)
 */
@ParametersAreNonnullByDefault
public abstract class AbstractColumnDef<T> implements SelectListItem<T> {

	@Nonnull private final String _dbColname;
	@Nonnull private final DbType<T> _dbType;
	@CheckForNull private TableDef _table;
	@Nonnull private String _fqName;

	// private final ColumnConverter<T,?> _conv;

	/**
	 * ctor.
	 */
	AbstractColumnDef(final String dbColname, final DbType<T> dbType) {
		super();
		_dbColname= dbColname;
		_dbType= dbType;
		_fqName = dbColname;
	}

    /**
	 * Yields a column assignment setting this column to the given value.
	 */
	@Nonnull
	ColumnValueAssignment<T> setInternal(@Nullable final T value) {
		return Sql.set( this, value);
	}

	/**
	 * Yields a column assignment setting this column to the value of the specified other column.
	 */
	@Nonnull
	Column2ColumnAssignment<T> setInternal(final AbstractColumnDef<T> other) {
		return Sql.set( this, other);
	}

	void setTable( final TableDef table ) {
        if ( _table != null ) {
            throw new IllegalStateException( "setTable was already called but needs to be called exactly once... " +
                    "most probably you did something wrong setting up your table-definitions." );
        }
        _table = table;
        _fqName = _table.getTableName() + "." + _dbColname;
    }

	//FIXME (CM): Check if this is used in HOI & SEV
	@CheckForNull
	public TableDef getTable() {
		return _table;
	}

    @Override
    public String name() {
        assertTableSet();
        return _dbColname;
    }

    @Override
    public String fqName() {
        assertTableSet();
        return _fqName;
    }

    protected void setFqName(@Nonnull final String fqName) {
        _fqName = fqName;
    }

    private void assertTableSet() {
        if (_table == null) {
            throw new IllegalStateException(
                    String.format("setTable of column %s was never called. youre table-setup is broken.", _fqName));
        }
    }

	@Override
	public boolean isColumnName() {
		return true;
	}

	@Override
	public DbType<T> type() {
		return _dbType;
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value equals the parameter value.
	 */
	@Nonnull
	public BooleanExpression eq(@Nullable final T value) {
		return Sql.eq( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value equals the value of the other column specified in the parameter.
	 */
	@Nonnull
	public BooleanExpression eq(final AbstractColumnDef<T> other) {
		return Sql.eq( this, other);
	}

	/**
     * Yields a condition suitable for use in WHERE and HAVING clauses.
     * Returns <code>true</code> if and only if the column's value equals the parameter value, iqnoring the case.
     */
    @Nonnull
    public BooleanExpression ieq(@Nullable final T value) {
        return Sql.ieq( this, value);
    }

    /**
     * Yields a condition suitable for use in WHERE and HAVING clauses.
     * Returns <code>true</code> if and only if the column's value equals the value of the other column specified in the parameter, iqnoring the case.
     */
    @Nonnull
    public BooleanExpression ieq(final AbstractColumnDef<T> other) {
        return Sql.ieq( this, other);
    }

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value does not equal the parameter value.
	 */
	@Nonnull
	public BooleanExpression neq(@Nullable final T value) {
		return Sql.neq( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is less than the parameter value.
	 */
	@Nonnull
	public BooleanExpression lt(final T value) {
		return Sql.lt( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is less than the value in the given column.
	 */
	@Nonnull
	public BooleanExpression lt(final AbstractColumnDef<T> other) {
		return Sql.lt( this, other);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is less than or equal to the parameter value.
	 */
	@Nonnull
	public BooleanExpression le(final T value) {
		return Sql.le( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is less than or equal to the value in the given column.
	 */
	@Nonnull
	public BooleanExpression le(final AbstractColumnDef<T> other) {
		return Sql.le( this, other);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is greater than the parameter value.
	 */
	@Nonnull
	public BooleanExpression gt(final T value) {
		return Sql.gt( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is greater than the value in the given column.
	 */
	@Nonnull
	public BooleanExpression gt(final AbstractColumnDef<T> other) {
		return Sql.gt( this, other);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is greater than or equal to the parameter value.
	 */
	@Nonnull
	public BooleanExpression ge(final T value) {
		return Sql.ge( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is greater than or equal to the value in the given column.
	 */
	@Nonnull
	public BooleanExpression ge(final AbstractColumnDef<T> other) {
		return Sql.ge( this, other);
	}

	public SelectListItem<T> distinct() {
	    return Sql.distinct(this);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is <code>null</code>.
	 */
	@Nonnull
	public BooleanExpression isNull() {
		return Sql.isNull( this);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is not <code>null</code>.
	 */
	@Nonnull
	public BooleanExpression isNotNull() {
		return Sql.isNotNull( this);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value equals one of the parameter values.
	 */
	@Nonnull
	public BooleanExpression in(final T... values) {
		return Sql.in( this, values);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is contained in the parameter values.
	 */
	@Nonnull
	public BooleanExpression in(final Iterable<T> values) {
		return Sql.in( this, values);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value is contained in the result value of the parameter subquery.
	 */
	@Nonnull
	public BooleanExpression in(final SubQuery<T> query) {
		return Sql.in( this, query);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value matches the parameter pattern.
	 * The pattern match is case-sensitive.
	 */
	@Nonnull
	public BooleanExpression like(final T value) {
		return Sql.like( this, value);
	}

	/**
	 * Yields a condition suitable for use in WHERE and HAVING clauses.
	 * Returns <code>true</code> if and only if the column's value matches the parameter pattern.
	 * The pattern match is case-insensitive.
	 */
	@Nonnull
	public BooleanExpression ilike(final T value) {
		return Sql.ilike( this, value);
	}

    @Override
    public <I> I accept(@Nonnull final OrderItemVisitor<I> visitor) {
        return visitor.visit(this);
    }

	@Override
	public int hashCode() {
		return _dbColname.hashCode();
	}

	@SuppressWarnings( "unchecked")
	@Override
	public boolean equals(final Object obj) throws ClassCastException{
	    // FIXME (CM, JK): https://github.com/greenhornet/freiheit_sqlapi/issues/4
		// die Konsequenz der aktuellen Implementierung ist, dass gilt: Table1.ID.equals Table2.ID
		//
		// ein Vergleich mit "instaneof" kostet viel Zeit, daher sparen wir uns
		// das hier
		// und er soll eine ehrliche ClassCastException werfen wenn was schief
		// gelaufen ist (fail-fast)
		return obj != null && _dbColname.equals( ((AbstractColumnDef<T>)obj)._dbColname);
	}

	@Override
	public String toString() {
		return _fqName;
	}

}
