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
package com.freiheit.sqlapi4j.generate.impl;

import java.sql.Types;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlStdConverter;
import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.query.clause.LockMode;
import com.freiheit.sqlapi4j.query.impl.ValueComparisonType;

public abstract class BasicSqlDialect implements SqlDialect {

	PsqlStdConverter _stdConverter= new PsqlStdConverter();
	ConverterRegistry _convRegistry;

	public BasicSqlDialect( ConverterRegistry parent) {
		_convRegistry= new ConverterRegistry( parent);
		_convRegistry.setDefaults( _stdConverter);
	}

	@Override
	public void addAnd( StringBuilder sb) {
		sb.append( " and ");
	}

	@Override
	public void addCloseParenthese( StringBuilder sb) {
		sb.append( ") ");
	}

	@Override
	public void addComparison( StringBuilder sb, ValueComparisonType cmpType, String colFqName, String quotedConvertedValue) {
	    switch (cmpType) {
        case IEQ:
            sb.append( "LOWER(" + colFqName + ")");
            addValueComp( sb, ValueComparisonType.EQ);
            sb.append( "LOWER(" + quotedConvertedValue + ")");
            break;
        default:
            sb.append( colFqName);
            addValueComp( sb, cmpType);
            sb.append( quotedConvertedValue);
	    }
	}

	protected void addValueComp( StringBuilder sb, ValueComparisonType cmpType) {
		switch( cmpType) {
		case EQ:
			sb.append( "=");
			break;
		case LT:
			sb.append( "<");
			break;
		case LE:
			sb.append( "<=");
			break;
		case GT:
			sb.append( ">");
			break;
		case GE:
			sb.append( ">=");
			break;
		case LIKE:
			sb.append( " like ");
			break;
		case ILIKE:
			sb.append( " ilike ");
			break;
		}
	}

	@Override
	public void addFrom( StringBuilder sb) {
		sb.append( " from ");
	}

	@Override
	public void addIsNotNull( StringBuilder sb) {
		sb.append( " is not NULL");
	}

	@Override
	public void addIsNull( StringBuilder sb) {
		sb.append( " is NULL");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addNot( StringBuilder sb) {
		sb.append( " not");
	}

	@Override
	public void addOpenParenthese( StringBuilder sb) {
		sb.append( " (");
	}

	@Override
	public void addOr( StringBuilder sb) {
		sb.append( " or ");
	}

	@Override
	public void addPreparedStatementPar( StringBuilder sb) {
		sb.append( "?");
	}

	@Override
	public void addQuote( StringBuilder sb) {
		sb.append( "'");
	}

	@Override
	public void addSelect( StringBuilder sb) {
		sb.append( "select ");
	}

	@Override
	public void addInsert( StringBuilder sb) {
		sb.append( "insert into ");
	}

	@Override
	public void addValues( StringBuilder sb) {
		sb.append( " values ");
	}

	@Override
	public void addSelectListSeparator( StringBuilder sb) {
		sb.append( ",");
	}

	@Override
	public void addWhere( StringBuilder sb) {
		sb.append( " where ");
	}

	@Override
	public <T> boolean needsQuote( DbType<T> dbType) {
		@SuppressWarnings("unchecked")
        ColumnConverter<T,?> conv= getConverterFor( (Class<? extends DbType<T>>)dbType.getClass());
		if( conv == null) {
			throw new IllegalArgumentException( "no column converter for db-type " + dbType.getClass());
		}
		switch( conv.getSqlType()) {
		case Types.CHAR:
		case Types.CLOB:
		case Types.DATE:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.TIME:
		case Types.TIMESTAMP:
		case Types.VARCHAR:
			return true;
		}
		return false;
	}

	@Override
	public <T> ColumnConverter<T,?> getConverterFor(final Class<? extends DbType<T>> dbType) {
		return _convRegistry.getConverter( dbType);
	}

	@Override
	public SqlStdConverter getStandardConverter() {
		return _stdConverter;
	}

	@Override
	public void addGroupBy( StringBuilder sb) {
		sb.append( " group by ");
	}

	@Override
	public void addHaving( StringBuilder sb) {
		sb.append( " having ");
	}

	@Override
	public void addOrderBy( StringBuilder sb) {
		sb.append( " order by ");
	}

	@Override
	public void addUpdate( StringBuilder sb) {
		sb.append( "update ");
	}

	@Override
	public void addDelete( StringBuilder sb) {
		sb.append( "delete ");
	}

	@Override
	public void addSet( StringBuilder sb) {
		sb.append( " set ");
	}

	@Override
	public void addAssign( StringBuilder sb) {
		sb.append( "=");
	}


	@Override
	public void addLimitAndOffset(StringBuilder sb, Integer offsetNum,
			Integer limitNum) {
		if (limitNum != null) {
			sb.append(" limit ").append(limitNum);
		}
		if (offsetNum != null) {
			sb.append(" offset "). append(offsetNum);
		}
		
	}
	
	@Override
	public void addIn( StringBuilder sb) {
		sb.append( " in");
	}

	@Override
	public void addCreateTable( StringBuilder sb) {
		sb.append( "create table ");
	}
	
	@Override
	public String sequenceNextvalCmd( String sequenceName ) {
		return "select NEXTVAL('"+sequenceName.toUpperCase()+"') as value";
	}
	
	@Override
	public String addLockMode(LockMode lockMode) {
		switch (lockMode) {
			case UPGRADE:
			case UPGRADE_NO_WAIT:
			case UPGRADE_SKIP_LOCKED:
				return " for update";
		}
		return "";
	}

}
