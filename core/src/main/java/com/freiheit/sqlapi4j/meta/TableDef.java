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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.FromDefVisitor;
import com.freiheit.sqlapi4j.query.impl.JoinDecl;
import com.freiheit.sqlapi4j.query.impl.OnPart;

/**
 * Metadata for a database table.
 *
 * @author Jörg Kirchhof (joerg@freiheit.com) (initial creation)
 */
@ParametersAreNonnullByDefault
public class TableDef implements FromDef {

	@Nonnull private final String _tableName;
	// private final String _seqName;

	@Nonnull private final AbstractColumnDef<?>[] _columns;
	@Nonnull private final Map<AbstractColumnDef<?>,Integer> _colIndizes = new HashMap<AbstractColumnDef<?>,Integer>();

	/**
	 * @param tableName the table name
	 * @param cols the table's column definitions
	 */
	public TableDef(final String tableName, final AbstractColumnDef<?>... cols) {
		super();
		_tableName= tableName;
		// _seqName= seqName;
		_columns= new AbstractColumnDef[cols.length];
		int i= 0;
		for( final AbstractColumnDef<?> col : cols) {
			col.setTable( this);
			_columns[i]= col;
			_colIndizes.put( col, Integer.valueOf( i));
			++i;
		}
        validateColumnNames();
	}

    /**
	 * @param tableName the table name
	 * @param cols the table's column definitions
	 */
	public TableDef(final String tableName, final List<AbstractColumnDef<?>> cols) {
		super();
		_tableName= tableName;
		// _seqName= seqName;
		_columns= new AbstractColumnDef[cols.size()];
		int i= 0;
		for( final AbstractColumnDef<?> col : cols) {
			col.setTable( this);
			_columns[i]= col;
			_colIndizes.put( col, Integer.valueOf( i));
			++i;
		}
        validateColumnNames();
	}

    private void validateColumnNames() {
        final Set<String> names = new HashSet<String>();
        for (final AbstractColumnDef<?> col : _columns) {
            final String name = col.fqName();
            if (names.contains(name)) {
                throw new IllegalArgumentException("column name '" + name + "' is not unique in table '" + _tableName + "'!");
            }
            names.add(name);
        }
    }

	/**
	 * Get all columns.
	 */
	@Nonnull
	public AbstractColumnDef<?>[] getColumns() {
		return _columns;
	}

	public String getTableName() {
		return _tableName;
	}

	/**
	 * Get the name of the sequence used for generating primary keys.
	 */
	// @Nonnull
	// public String getSeqName() {
	// return _seqName;
	// }
	/**
	 * Get the column index.
	 */
	public int getColIndex(final AbstractColumnDef<?> col) {
		final Integer indx= _colIndizes.get( col);
		if( indx == null) {
			return -1;
		}
		return indx.intValue();
	}

	/**
	 * Get all column names interspersed by commas as one single string, suitable for use in SELECT clauses.
	 *
	 * @see #joinAllColnames(String)
	 */
	@Nonnull
	public String getColumnNamesString() {
		return joinColnames( ",", _columns);
	}

	/**
	 * Get all column names interspersed by the specified separator as one single string.
	 *
	 * @see #joinColnames(String, AbstractColumnDef...)
	 */
	@Nonnull
	public String joinAllColnames(final String sep) {
		return joinColnames( sep, _columns);

	}

	/**
	 * Get the names of the specified columns interspersed by the specified separator as one single string.
	 */
	@Nonnull
	public String joinColnames(final String sep, final AbstractColumnDef<?>... values) {
		final StringBuilder sb= new StringBuilder();
		for( int i= 0; i < values.length; ++i) {
			if( i > 0) {
				sb.append( sep);
			}
			sb.append( values[i].name());
		}
		return sb.toString();
	}

	@Nonnull
	public OnPart leftOuterJoin(@Nonnull final TableDef right) {
	    return JoinDecl.makeLeftOuterJoin(this, right);
	}

    @Nonnull
    public TableAlias alias(@Nonnull final String alias) {
        return new TableAlias(this, alias);
    }

    public <T> T accept(@Nonnull final FromDefVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
