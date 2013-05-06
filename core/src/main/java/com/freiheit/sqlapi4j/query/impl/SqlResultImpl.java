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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.query.NonnullSelectListItem;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.SelectResult;
import com.freiheit.sqlapi4j.query.SqlResultRow;

class SqlResultImpl implements SelectResult {

	private final SqlDialect _dialect;
	private final SelectListItem<?>[] _selectItems;
	private final ResultSet _rs;
	private final Statement _statement;

	private Map<String,Integer> _colIndex= null;
	private List<String> _colnames;

	private Boolean _hasNext= null;
	private SqlResultRowImpl _lastRow= null;

	public SqlResultImpl( final SqlDialect dialect, final SelectListItem<?>[] selectItems, final ResultSet rs, final Statement statement ) {
		_dialect= dialect;
		_selectItems= selectItems;
		_rs= rs;
		_statement = statement;
	}

	private void initMetadata() throws SQLException {
		_colIndex= new HashMap<String,Integer>();
		_colnames= new ArrayList<String>();
		for( int i= 0; i < _selectItems.length; i++) {
			SelectListItem<?> selectListItem= _selectItems[i];
			_colIndex.put( selectListItem.fqName(), Integer.valueOf( i));
			_colnames.add( selectListItem.fqName());
		}
	}

	@Override
	public Iterable<String> columnDefs() throws SQLException {
		if( _colIndex == null) {
			initMetadata();
		}
		return _colnames;
	}

	@Override
	public int getNofColumns() throws SQLException {
		if( _colIndex == null) {
			initMetadata();
		}
		return _colIndex.keySet().size();
	}

	@Override
	public boolean hasNext() throws SQLException {
		if( _hasNext != null) {
            return _hasNext.booleanValue();
        }
		boolean hasNext= determineNext();
		return hasNext;
	}

	@Override
	public SqlResultRow next() throws SQLException {
		SqlResultRowImpl res= _lastRow;
		determineNext();
		return res;
	}

	private boolean determineNext() throws SQLException {
		if( _colIndex == null) {
			initMetadata();
		}
		boolean hasNext= _rs.next();
		_hasNext= Boolean.valueOf( hasNext);
		if( hasNext) {
			_lastRow= getRowData();
		} else {
			_lastRow= null;
		}
		return hasNext;
	}

	private SqlResultRowImpl getRowData() throws SQLException {
		int nofColumns= _colIndex.keySet().size();
		Object[] row= new Object[nofColumns];
		for( int i= 0; i < nofColumns; ++i) {
			row[i]= _rs.getObject( i + 1);
		}
		return new SqlResultRowImpl( row);
	}

	@Override
	public void close() throws SQLException {
	    try {
	        _rs.close();
	    }
	    catch ( Exception ex ) {
	        // Result-Sets don't like to be closed twice.
	        // We ignore any error on closing the ResultSet as closing the statement will release all resources anyways.
	    }
        _statement.close();
	}

	private class SqlResultRowImpl implements SqlResultRow {

		private final Object[] _row;

		public SqlResultRowImpl( final Object[] row) {
			_row= row;
		}

		@Override
		public <T, S extends SelectListItem<T>> T get(S item) {
			Integer index= _colIndex.get( item.fqName());
			if( index == null) {
				throw new IllegalArgumentException( "column " + item + " does not exist in result set");
			}
			@SuppressWarnings("unchecked")
            ColumnConverter<T,Object> conv= (ColumnConverter<T,Object>)_dialect.getConverterFor( (Class<? extends DbType<T>>)item.type().getClass());
			if( conv == null) {
				throw new IllegalStateException( "no converter for column definition " + item + " with db type " + item.type().getClass());
			}
			return conv.fromDb( _row[index.intValue()], item.type());
		}

		@Override
		public <T, S extends NonnullSelectListItem<T>> T get(S item) {
		    final T value = get((SelectListItem<T>) item);
		    if (value == null) {
		        throw new IllegalStateException("Value of column " + item.fqName() + " is null but should not.");
		    }
		    return value;
		}

		@Override
		public Iterable<?> columns() {
			Object[] res= new Object[_row.length];
			for( int i= 0; i < _row.length; ++i) {
				res[i]= getConvertedValue( _row[i], _selectItems[i]);
			}
			return Arrays.asList( res);
		}

		@SuppressWarnings("unchecked")
        private <T> ColumnConverter<T,Object> getConverterFor( Object dbValue, SelectListItem<T> item) {
			return (ColumnConverter<T,Object>)_dialect.getConverterFor( (Class<? extends DbType<T>>)item.type().getClass());
		}

		@SuppressWarnings( "unchecked")
		private <T> T getConvertedValue( Object dbValue, SelectListItem<T> item) {
			ColumnConverter<T,Object> conv= getConverterFor( dbValue, item);
			if( conv == null) {
				return (T)dbValue;
			}
			return conv.fromDb( dbValue, item.type());
		}

		@Override
		public String toString() {
			return "" + Arrays.asList( _row);
		}

	}

}
