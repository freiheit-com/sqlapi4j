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

import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.freiheit.sqlapi4j.generate.SqlDialect;
import com.freiheit.sqlapi4j.generate.SqlGenerator;
import com.freiheit.sqlapi4j.meta.AbstractColumnDef;
import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.meta.TableAlias;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.BooleanExpressionVisitor;
import com.freiheit.sqlapi4j.query.Column2ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.ColumnValueAssignment;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.FromDefVisitor;
import com.freiheit.sqlapi4j.query.NullOrderItem;
import com.freiheit.sqlapi4j.query.OrderItem;
import com.freiheit.sqlapi4j.query.SelectListItem;
import com.freiheit.sqlapi4j.query.Sql;
import com.freiheit.sqlapi4j.query.Sql.And;
import com.freiheit.sqlapi4j.query.Sql.BooleanCombination;
import com.freiheit.sqlapi4j.query.Sql.ColumnComparisonOperation;
import com.freiheit.sqlapi4j.query.Sql.ColumnEquals;
import com.freiheit.sqlapi4j.query.Sql.ColumnEqualsIgnoreCase;
import com.freiheit.sqlapi4j.query.Sql.ColumnGreaterThan;
import com.freiheit.sqlapi4j.query.Sql.ColumnLessThan;
import com.freiheit.sqlapi4j.query.Sql.InSubselectExpression;
import com.freiheit.sqlapi4j.query.Sql.InValuesExpression;
import com.freiheit.sqlapi4j.query.Sql.IsNotNull;
import com.freiheit.sqlapi4j.query.Sql.IsNull;
import com.freiheit.sqlapi4j.query.Sql.Not;
import com.freiheit.sqlapi4j.query.Sql.Or;
import com.freiheit.sqlapi4j.query.Sql.Ordering;
import com.freiheit.sqlapi4j.query.Sql.PairInExpression;
import com.freiheit.sqlapi4j.query.Sql.ParameterPair;
import com.freiheit.sqlapi4j.query.impl.Column2ColumnComparison;
import com.freiheit.sqlapi4j.query.impl.ColumnComparison;
import com.freiheit.sqlapi4j.query.impl.JoinDecl;
import com.freiheit.sqlapi4j.query.impl.ResultFlags;
import com.freiheit.sqlapi4j.query.impl.ValueComparisonType;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;

public class SqlGeneratorImpl implements SqlGenerator {

	@Override
	public String generateQueryString( final SqlDialect dialect, final SelectStatement abstractSyntax, final ResultFlags resFlags, final PreparedStatementData preparedStatementData) {
		return generateSql( preparedStatementData, dialect, abstractSyntax, resFlags);
	}

	@Override
	public String generatePreparedStatement( final SqlDialect dialect, final SelectStatement abstractSyntax) {
		return generateSql( new PreparedStatementData( true), dialect, abstractSyntax, new ResultFlags());
	}

	public String generateSql( final PreparedStatementData preparedStatementData, final SqlDialect dialect, final SelectStatement syntax, final ResultFlags resFlags) {
		checkColumnWildcard( syntax.getSelectItems(), resFlags);
		final StringBuilder sb= new StringBuilder();
		dialect.addSelect( sb);
        final FromDef[] fromDefs = syntax.getFromDef();
        final String indexName = syntax.getIndexName();
        if (indexName != null && !indexName.isEmpty() && fromDefs.length == 1) {
            dialect.addIndexToUse(sb, indexName, fromDefs);
        }
		addSelectListItems( dialect, sb, syntax.getSelectItems());
		addFromPart( dialect, syntax.getFromDef(), sb, preparedStatementData);
		final BooleanExpression where= syntax.getWhere();
		if( where != null) {
			dialect.addWhere( sb);
			final BuildBooleanExpressionsClauseVisitor visitor= new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect);
			where.accept( visitor);
		}
		final SelectListItem<?>[] groupItems= syntax.getGroupItems();
		if( groupItems != null && groupItems.length > 0) {
			dialect.addGroupBy( sb);
			addSelectListItems( dialect, sb, groupItems);
		}

		final BooleanExpression having = syntax.getHaving();
		if (having != null) {
			dialect.addHaving(sb);
			final BuildBooleanExpressionsClauseVisitor visitor= new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect);
			having.accept( visitor);
		}

		final OrderItem[] orderItems = syntax.getOrderItems();
		if( orderItems != null && orderItems.length > 0) {
			dialect.addOrderBy(sb);
			addOrderItems(dialect, sb, orderItems);
		}

		if( syntax.getLimitNum() != null || syntax.getOffsetNum() != null ) {
			dialect.addLimitAndOffset( sb, syntax.getOffsetNum(), syntax.getLimitNum());
		}


		if (syntax.getLockMode() != null) {
			sb.append(dialect.addLockMode(syntax.getLockMode()));
		}

		return sb.toString();
	}

	// SelectListItem.ALL wird erst mal wieder rausgenommen
	private void checkColumnWildcard( final SelectListItem<?>[] items, final ResultFlags resFlags) {
		//		for( int i= 0; i < items.length; ++i) {
		//			if( items[i] == SelectListItem.ALL) {
		//				resFlags.setNeedsMetadata( true);
		//				return;
		//			}
		//		}
	}

	private static void addSelectListItems( final SqlDialect dialect, final StringBuilder sb, final SelectListItem<?>... items) {
		for( int i= 0; i < items.length; ++i) {
			//sb.append( items[i].name());
			sb.append( items[i].fqName());
			if( i < items.length - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

    private static void addOrderItems(final SqlDialect dialect, final StringBuilder sb, final OrderItem... orders) {
        final OrderItem.OrderItemVisitor<Void> visitor = new OrderItem.OrderItemVisitor<Void>() {
            @Override
            public Void visit(@Nonnull final SelectListItem<?> item) {
                addSelectListItems(dialect, sb, item);
                return null;
            }

            @Override
            public Void visit(@Nonnull final Ordering<?> ordering) {
                addSelectListItems(dialect, sb, ordering.getItem());
                dialect.addOrderDirection(sb, ordering.getDirection());
                final NullOrderItem.NullOrder nullOrder = ordering.getNullOrder();
                if (nullOrder != null) {
                    dialect.addNullOrder(sb, nullOrder);
                }
                return null;
            }
        };
        for( int i= 0; i < orders.length; ++i) {
            orders[i].accept(visitor);
            if (i < orders.length - 1) {
                dialect.addSelectListSeparator(sb);
            }
        }
    }

	/**
	 * Note: This class is not static, because it needs to recurse into the SqlGenerator when creating subqueries.
	 *
	 * @author Klas Kalass (klas.kalass@freiheit.com)
	 *
	 */
	private class BuildBooleanExpressionsClauseVisitor implements BooleanExpressionVisitor<Void> {

		private final PreparedStatementData _preparedStatement;
		private final StringBuilder _sb;
		private final SqlDialect _dialect;
		private BooleanExpression _topLevelExpr= null;

		public BuildBooleanExpressionsClauseVisitor( final PreparedStatementData preparedStatement, final StringBuilder sb, final SqlDialect dialect) {
			_preparedStatement= preparedStatement;
			_sb= sb;
			_dialect= dialect;
		}

		@Override
		public Void visit( final IsNull isNull) {
			_sb.append( isNull.getCol().fqName());
			_dialect.addIsNull( _sb);
			return null;
		}

		@Override
		public Void visit( final IsNotNull isNotNull) {
			_sb.append( isNotNull.getCol().fqName());
			_dialect.addIsNotNull( _sb);
			return null;
		}

		@Override
		public Void visit( final ColumnEquals<?> eq) {
			buildComparison( eq);
			return null;
		}

        @Override
        public Void visit(final ColumnEqualsIgnoreCase<?> ieq) {
            buildComparison(ieq);
            return null;
        }

		@Override
		public Void visit( final ColumnLessThan<?> eq) {
			buildComparison( eq);
			return null;
		}

		@Override
		public Void visit( final ColumnGreaterThan<?> eq) {
			buildComparison( eq);
			return null;
		}

		@Override
		public Void visit( final ColumnComparisonOperation<?> comp) {
			buildComparison( comp);
			return null;
		}

		private void buildComparison( final ColumnComparison<?> comp) {
			final String quotedConvertedValue= getQuotedConvertedValue( _dialect, comp.getCol(), comp.getValue(), _preparedStatement);
			_dialect.addComparison( _sb, comp.getComparison(), comp.getCol().fqName(), quotedConvertedValue);
		}

		@Override
		public Void visit( final Not not) {
			_dialect.addNot( _sb);
			_dialect.addOpenParenthese( _sb);
			not.getExpression().accept( this);
			_dialect.addCloseParenthese( _sb);
			return null;
		}

		@Override
		public Void visit( final And and) {
			return visit( and, true);
		}

		@Override
		public Void visit( final Or or) {
			return visit( or, false);
		}

		private Void visit( final BooleanCombination n, final boolean isAnd) {
			final boolean amITopLevel= _topLevelExpr == null;
			if( _topLevelExpr == null) {
				_topLevelExpr= n;
			} else {
				_dialect.addOpenParenthese( _sb);
			}
			final Iterator<BooleanExpression> it= n.getExpr().iterator();
			while( it.hasNext()) {
				final BooleanExpression exp= it.next();
				exp.accept( this);
				if( it.hasNext()) {
					if( isAnd) {
						_dialect.addAnd( _sb);
					} else {
						_dialect.addOr( _sb);
					}
				}
			}
			if( !amITopLevel) {
				_dialect.addCloseParenthese( _sb);
			}
			return null;
		}

		@Override
		public Void visit( final InValuesExpression<?> expr) {
			visitIn( expr);
			return null;
		}

		public <T> void visitIn( final InValuesExpression<T> expr) {
			_sb.append( expr.getColumn().fqName());
			_dialect.addIn( _sb);
			_dialect.addOpenParenthese( _sb);
			addQuotedConvertedValues( _dialect, _sb, expr.getColumn(), expr.getValues().iterator(), _preparedStatement);
			_dialect.addCloseParenthese( _sb);
		}

		@Override
		public Void visit( final InSubselectExpression<?> expr) {
			_sb.append( expr.getColumn().fqName());
			_dialect.addIn( _sb);
			_dialect.addOpenParenthese( _sb);
			final ResultFlags resFlags= new ResultFlags();
			final SelectStatement subquery = expr.getSubquery().stmt();
	        final String subqueryString = SqlGeneratorImpl.this.generateQueryString(_dialect, subquery, resFlags, _preparedStatement);
			_sb.append( subqueryString );
			_dialect.addCloseParenthese( _sb);
			return null;
		}

		@Override
		public Void visit( final Column2ColumnComparison<?> comp) {
			_dialect.addComparison( _sb, comp.getComparison(), comp.getCol().fqName(), comp.getOtherColumn().fqName());
			return null;
		}

        @Override
        public Void visit(final PairInExpression<?, ?> expr) {
            if (_dialect.supportsRowValueConstructorSyntaxInInList()) {
                _sb.append("(").append(expr.getColumn1().fqName()).append(",").append(expr.getColumn2().fqName()).append(")");
                _dialect.addIn(_sb);
                _dialect.addOpenParenthese( _sb);
                boolean first=true;
                for (final ParameterPair<?, ?> p:expr.getParameterPairs()) {
                    if (!first) {
                        _sb.append(",");
                    }
                    _dialect.addOpenParenthese( _sb);
                    addQuotedConvertedValue(_dialect, _sb, expr.getColumn1(), p.getLeft(), _preparedStatement);
                    _sb.append(",");
                    addQuotedConvertedValue(_dialect, _sb, expr.getColumn2(), p.getRight(), _preparedStatement);
                    _dialect.addCloseParenthese( _sb);
                    first=false;
                }
                _dialect.addCloseParenthese( _sb);
            } else {
                _dialect.addOpenParenthese( _sb);
                boolean first=true;
                for (final ParameterPair<?, ?> p:expr.getParameterPairs()) {
                    if (!first) {
                        _dialect.addOr(_sb);
                    }
                    _dialect.addOpenParenthese( _sb);
                    _dialect.addComparison( _sb, ValueComparisonType.EQ, expr.getColumn1().fqName(), getQuotedConvertedValue( _dialect, expr.getColumn1(), p.getLeft(), _preparedStatement));
                    _dialect.addAnd(_sb);
                    _dialect.addComparison( _sb, ValueComparisonType.EQ, expr.getColumn2().fqName(), getQuotedConvertedValue( _dialect, expr.getColumn2(), p.getRight(), _preparedStatement));
                    _dialect.addCloseParenthese( _sb);
                    first=false;
                }
                _dialect.addCloseParenthese( _sb);
            }
            return null;
        }
	}

	@Override
	public String generateInsertStatement( final SqlDialect dialect, final TableDef table, final List<ColumnAssignment<?>[]> assignmentList, final FromDef[] fromDefs, final BooleanExpression where, final PreparedStatementData preparedStatementData) {

		final StringBuilder sb= new StringBuilder();
		dialect.addInsert( sb);
		sb.append( table.getTableName());
		dialect.addOpenParenthese( sb);
		addSelectListItems( dialect, sb, assignmentList, false, 0);
		dialect.addCloseParenthese( sb);

		if( fromDefs == null) {

			dialect.addValues( sb);

			final Iterator<ColumnAssignment<?>[]> it= assignmentList.iterator();
			while( it.hasNext()) {
				final ColumnAssignment<?>[] assignments= it.next();
				dialect.addOpenParenthese( sb);
				addRhsOfColumnAssignments( dialect, sb, assignments, preparedStatementData);
				dialect.addCloseParenthese( sb);
				if( it.hasNext()) {
					dialect.addSelectListSeparator( sb);
				}
			}
		} else {
			dialect.addSelect( sb);
			//dialect.addOpenParenthese( sb);
			addRhsOfColumnAssignments( dialect, sb, assignmentList.get( 0), preparedStatementData);
			//dialect.addCloseParenthese( sb);
			addFromPart( dialect, fromDefs, sb, preparedStatementData);
			if( where != null) {
				dialect.addWhere( sb);
				final BuildBooleanExpressionsClauseVisitor visitor= new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect);
				where.accept( visitor);
			}
		}

		return sb.toString();
	}

	private static void addSelectListItems( final SqlDialect dialect, final StringBuilder sb, final List<ColumnAssignment<?>[]> assignments, final boolean addTableNames, final int fromRow) {
		addSelectListItems( dialect, sb, assignments.get( fromRow), false);
	}

	private static void addSelectListItems( final SqlDialect dialect, final StringBuilder sb, final ColumnAssignment<?>[] assignments, final boolean addTableNames) {
		for( int i= 0; i < assignments.length; ++i) {
			sb.append( addTableNames ? assignments[i].getLhsColumn().fqName() : assignments[i].getLhsColumn().name());
			if( i < assignments.length - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

	//	private void addRhsSelectListItems( SqlDialect dialect, StringBuilder sb, ColumnAssignment<?>[] assignments, boolean addTableNames) {
	//		for( int i= 0; i < assignments.length; ++i) {
	//			sb.append( addTableNames ? assignments[i].getRhsColumn().fqName() : assignments[i].getLhsColumn().name());
	//			if( i < assignments.length - 1) dialect.addSelectListSeparator( sb);
	//		}
	//	}

	@Override
	public String generateUpdateStatement( final SqlDialect dialect, final TableDef table, final ColumnAssignment<?>[] assignments, final BooleanCombination boolComb, final PreparedStatementData preparedStatementData) {

		final StringBuilder sb= new StringBuilder();
		dialect.addUpdate( sb);
		sb.append( table.getTableName());
		dialect.addSet( sb);
		addAssignments( dialect, sb, assignments, preparedStatementData);
		if (boolComb != null) {
		    dialect.addWhere( sb);
		    final BuildBooleanExpressionsClauseVisitor visitor= new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect);
		    boolComb.accept( visitor);
		}

		return sb.toString();
	}

	@Override
    public String generateDeleteStatement( final SqlDialect dialect, final TableDef table, final BooleanCombination boolComb, final PreparedStatementData preparedStatementData) {

		final StringBuilder sb= new StringBuilder();
		dialect.addDelete( sb);
		dialect.addFrom( sb);
		sb.append( table.getTableName());
		if (boolComb != null) {
		    dialect.addWhere( sb);
		    final BuildBooleanExpressionsClauseVisitor visitor= new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect);
		    boolComb.accept( visitor);
		}

		return sb.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String generateCreateTableStatement( final SqlDialect dialect, final TableDef table) {
		final StringBuilder sb= new StringBuilder();
		dialect.addCreateTable( sb);
		sb.append( table.getTableName() + " ");
		final AbstractColumnDef<?>[] cols= table.getColumns();
		dialect.addOpenParenthese( sb);
		for( int i= 0; i < cols.length; ++i) {
			final AbstractColumnDef<?> col= cols[i];
			sb.append( col.name() + " ");
			final ColumnConverter<?,?> conv= getConverterFor( dialect, col);
			sb.append( conv.getSqlTypeDeclaration( (DbType)col.type()));
			//sb.append( getSqlTypeDeclaration( cols[i]));
			if( i < cols.length - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
		dialect.addCloseParenthese( sb);
		return sb.toString();
	}

	@Override
	public String generateSequenceNextvalCmd( final SqlDialect dialect, final String sequenceName) {
		return dialect.sequenceNextvalCmd( sequenceName );
	}

	private void addFromPart(final SqlDialect dialect, final FromDef[] fromDefs, final StringBuilder sb, final PreparedStatementData preparedStatementData) {
		dialect.addFrom( sb);
		final int fromDefLength= fromDefs.length;
		for( int i= 0; i < fromDefLength; ++i) {
			final FromDef fromDef= fromDefs[i];

            fromDef.accept(new FromDefVisitor<Void>() {
                public Void visit(@Nonnull final TableDef tableDef) {
                    sb.append( fromDef.getTableName());
                    return null;
                }

                public Void visit(@Nonnull final JoinDecl joinDecl) {

                    sb.append(joinDecl.getTable1().getTableName());

                    switch (joinDecl.getJoinType()) {
                        case LEFT_OUTER_JOIN: dialect.addLeftOuterJoin(sb); break;
                        case RIGHT_OUTER_JOIN: dialect.addRightOuterJoin(sb); break;
                        case INNER_JOIN: dialect.addInnerJoin(sb); break;
                        case FULL_OUTER_JOIN: dialect.addFullOuterJoin(sb); break;
                        default: throw new UnsupportedOperationException();
                    }

                    sb.append(joinDecl.getTable2().getTableName());
                    dialect.addJoinOn(sb);

                    final BooleanExpression simpleJoinExpr = eqForce(joinDecl.getColumn1(), joinDecl.getColumn2());
                    final BooleanExpression joinExpr = joinDecl.getAdditionalExpr() == null
                            ? simpleJoinExpr
                            : Sql.and(simpleJoinExpr, joinDecl.getAdditionalExpr());

                    joinExpr.accept(new BuildBooleanExpressionsClauseVisitor( preparedStatementData, sb, dialect));

                    return null;
                }

                public Void visit(@Nonnull final TableAlias tableAlias) {
                    sb.append( fromDef.getTableName());
                    return null;
                }

                /**
                 * Force comparison of columns of unknown types.
                 * TODO: Remove necessity to force the comparison.
                 */
                private <T> BooleanExpression eqForce(AbstractColumnDef col1, AbstractColumnDef col2) {
                    return Sql.eq((AbstractColumnDef<T>)col1, (AbstractColumnDef<T>)col2);
                }
            });


            if (i < fromDefLength - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

	private static void addAssignments( final SqlDialect dialect, final StringBuilder sb, final ColumnAssignment<?>[] assignments, final PreparedStatementData preparedStatementData) {
		for( int i= 0; i < assignments.length; ++i) {
			final ColumnAssignment<?> assignment= assignments[i];
			sb.append( assignment.getLhsColumn().name());
			dialect.addAssign( sb);
			switch( assignment.getAssignmentType()) {
			case VALUE_ASSIGNMENT:
				addQuotedConvertedValue( dialect, sb, assignment.getLhsColumn(), ((ColumnValueAssignment<?>)assignment).getValue(), preparedStatementData);
				break;
			case COLUMN_ASSIGNMENT:
				final AbstractColumnDef<?> rhs= ((Column2ColumnAssignment<?>)assignment).getRhsColumn();
				sb.append( rhs.fqName());
			}
			if( i < assignments.length - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

	private static interface ColumnDevProvider {
		int getNofValues();

		Object getValue( int i);

		AbstractColumnDef<?> getColDef( int i);
	}

	private static void addRhsOfColumnAssignments( final SqlDialect dialect, final StringBuilder sb, final ColumnAssignment<?>[] assignments, final PreparedStatementData preparedStatementData) {
		for( int i= 0; i < assignments.length; ++i) {
			final ColumnAssignment<?> assignment= assignments[i];
			switch( assignment.getAssignmentType()) {
			case VALUE_ASSIGNMENT:
				addQuotedConvertedValue( dialect, sb, assignment.getLhsColumn(), ((ColumnValueAssignment<?>)assignment).getValue(), preparedStatementData);
				break;
			case COLUMN_ASSIGNMENT:
				final AbstractColumnDef<?> rhs= ((Column2ColumnAssignment<?>)assignment).getRhsColumn();
				sb.append( rhs.fqName());
			}
			if( i < assignments.length - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

	@SuppressWarnings( "unused")
	private static void addQuotedConvertedValues( final SqlDialect dialect, final StringBuilder sb, final ColumnDevProvider cols, final PreparedStatementData preparedStatementData) {
		for( int i= 0; i < cols.getNofValues(); ++i) {
			final AbstractColumnDef<?> col= cols.getColDef( i);
			final Object value= cols.getValue( i);
			addQuotedConvertedValue( dialect, sb, col, value, preparedStatementData);
			if( i < cols.getNofValues() - 1) {
                dialect.addSelectListSeparator( sb);
            }
		}
	}

	private static <T> void addQuotedConvertedValues( final SqlDialect dialect, final StringBuilder sb, final SelectListItem<T> col, final Iterator<T> it, final PreparedStatementData preparedStatementData) {
		while( it.hasNext()) {
			final T value= it.next();
			addQuotedConvertedValue( dialect, sb, col, value, preparedStatementData);
			if( it.hasNext()) {
				dialect.addSelectListSeparator( sb);
			}
		}
	}

	protected static void addQuotedConvertedValue( final SqlDialect dialect, final StringBuilder sb, final SelectListItem<?> col, final Object value, final PreparedStatementData preparedStatementData) {
		final boolean needsQuote= dialect.needsQuote( col.type());
		final Object dbValue= convertToDb( dialect, col.type(), value);
		if( preparedStatementData.isPreparedStatement()) {
			dialect.addPreparedStatementPar( sb);
			preparedStatementData.collectValue( dbValue);
		} else {
			if( needsQuote) {
                dialect.addQuote( sb);
            }
			sb.append( escapeQuotes( dbValue));
			if( needsQuote) {
                dialect.addQuote( sb);
            }
		}
	}

	protected static String getQuotedConvertedValue( final SqlDialect dialect, final SelectListItem<?> col, final Object value, final PreparedStatementData preparedStatementData) {
		final StringBuilder sb= new StringBuilder();
		addQuotedConvertedValue( dialect, sb, col, value, preparedStatementData);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
    private static <T> ColumnConverter<T,?> getConverterFor( final SqlDialect dialect, final AbstractColumnDef<T> col) {
		return dialect.getConverterFor( (Class<? extends DbType<T>>)col.type().getClass());
	}

	@CheckForNull
	@SuppressWarnings( { "unused" })
	private static <T> String getSqlTypeDeclaration( final SqlDialect dialect, final AbstractColumnDef<T> col) {
		@SuppressWarnings("unchecked")
        final ColumnConverter<T, ?> converter = dialect.getConverterFor( (Class<? extends DbType<T>>)col.type().getClass());
        return converter == null ? null : converter.getSqlTypeDeclaration( col.type());
	}

	@CheckForNull
	@SuppressWarnings( "unchecked")
	private static <T> Object convertToDb( final SqlDialect dialect, final DbType<T> dbType, final Object value) {
		final ColumnConverter<T, ?> converter = dialect.getConverterFor( (Class<? extends DbType<T>>)dbType.getClass());
        return converter == null ? null : converter.toDb( (T)value, dbType);
	}

	private static String escapeQuotes( final Object str) {
		if( str == null) {
			return "NULL";
		}
		// FIXME (FL): https://github.com/greenhornet/freiheit_sqlapi/issues/13
		//Das reicht nicht!!! Man kann z.B. auch mit einem Backslash escapen...
		return str.toString().replace( "'", "''");
	}

	public static void main( final String[] args) {
		System.out.println( escapeQuotes( "was'n das?"));
	}

}
