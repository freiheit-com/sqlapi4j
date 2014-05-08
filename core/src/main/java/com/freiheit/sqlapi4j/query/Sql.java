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
package com.freiheit.sqlapi4j.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.meta.AbstractColumnDef;
import com.freiheit.sqlapi4j.meta.ColumnDef;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.query.impl.Column2ColumnAssignmentImpl;
import com.freiheit.sqlapi4j.query.impl.Column2ColumnComparison;
import com.freiheit.sqlapi4j.query.impl.ColumnComparison;
import com.freiheit.sqlapi4j.query.impl.ColumnValueAssignmentImpl;
import com.freiheit.sqlapi4j.query.impl.ValueComparisonType;

/**
 * Helper for building SQL-Constructs.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com) (initial creation)
 */
@ParametersAreNonnullByDefault
public class Sql {

    /**
     * This method combines several conditions by AND into a new condition.
     * The resulting condition is <code>true</code> if and only if all parameter conditions are <code>true</code>.
     *
     * @param exprList the conditions
     * @return the combined condition
     */
    @Nonnull
	public static BooleanExpression and(final BooleanExpression... exprList) {
		return new And( exprList);
	}

	/**
	 * This method combines several conditions by OR into a new condition.
	 * The resulting condition is <code>true</code> if and only if at least one of the parameter conditions is <code>true</code>.
	 *
	 * @param exprList the conditions
	 * @return the combined condition
	 */
    @Nonnull
	public static BooleanExpression or(final BooleanExpression... exprList) {
		return new Or( exprList);
	}

	/**
	 * This method negates a condition.
	 * The resulting condition is <code>true</code> if and only if the parameter condition is <code>false</code>.
	 *
	 * @param expression the condition
	 * @return the negated condition
	 */
    @Nonnull
	public static BooleanExpression not(final BooleanExpression expression) {
		return new Not( expression);
	}

	/**
	 * This method creates a condition filtering only those rows whose value in column col equals the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression eq(final SelectListItem<T> col, @Nullable final T value) {
		return new ColumnEquals<T>( col, value);
	}

    /**
     * This method creates a condition filtering only those rows whose value in column col equals the value in the specified other column.
     */
    @Nonnull
	public static <T> BooleanExpression eq(final SelectListItem<T> col, final SelectListItem<T> other) {
		return new Column2ColumnComparison<T>( ValueComparisonType.EQ, col, other);
	}

    /**
     * This method creates a condition filtering only those rows whose value in column col equals the parameter value, ignoring the case
     */
    @Nonnull
    public static <T> BooleanExpression ieq(final SelectListItem<T> col, @Nullable final T value) {
        return new ColumnEqualsIgnoreCase<T>( col, value);
    }

    /**
     * This method creates a condition filtering only those rows whose value in column col equals the value in the specified other column, ignoring the case
     */
    @Nonnull
    public static <T> BooleanExpression ieq(final SelectListItem<T> col, final SelectListItem<T> other) {
        return new Column2ColumnComparison<T>( ValueComparisonType.IEQ, col, other);
    }

	/**
	 * This method creates a condition filtering only those rows whose value in column col does not equal the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression neq(final SelectListItem<T> col, @Nullable final T value) {
		return new Not( new ColumnEquals<T>( col, value));
	}

	/**
	 * This method creates a condition filtering only those rows whose value in column col is less than the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression lt(final SelectListItem<T> col, final T value) {
		return new ColumnLessThan<T>( col, value);
	}

	/**
	 * This method creates a condition filtering only those rows whose value in column col is less than the value in column other.
	 */
    @Nonnull
	public static <T> BooleanExpression lt(final SelectListItem<T> col, final SelectListItem<T> other) {
		return new Column2ColumnComparison<T>( ValueComparisonType.LT, col, other);
	}

	/**
	 * This method creates a condition filtering only those rows whose value in column col is less than or equal to the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression le(final SelectListItem<T> col, final T value) {
		return new Not(new ColumnGreaterThan<T>( col, value));
	}

    /**
     * This method creates a condition filtering only those rows whose value in column col is less than or equal to the value in column other.
     */
    @Nonnull
    public static <T> BooleanExpression le(final SelectListItem<T> col, final SelectListItem<T> other) {
        return new Column2ColumnComparison<T>( ValueComparisonType.LE, col, other);
    }

    /**
	 * This method creates a condition filtering only those rows whose value in column col is greater than the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression gt(final SelectListItem<T> col, final T value) {
		return new ColumnGreaterThan<T>( col, value);
	}

    /**
     * This method creates a condition filtering only those rows whose value in column col is less than the value in column other.
     */
    @Nonnull
    public static <T> BooleanExpression gt(final SelectListItem<T> col, final SelectListItem<T> other) {
        return new Column2ColumnComparison<T>( ValueComparisonType.GT, col, other);
    }

    /**
	 * This method creates a condition filtering only those rows whose value in column col is greater than or equal to the parameter value.
	 */
    @Nonnull
	public static <T> BooleanExpression ge(final SelectListItem<T> col, final T value) {
		return new Not( new ColumnLessThan<T>( col, value));
	}

    /**
     * This method creates a condition filtering only those rows whose value in column col is less than or equal to the value in column other.
     */
    @Nonnull
    public static <T> BooleanExpression ge(final SelectListItem<T> col, final SelectListItem<T> other) {
        return new Column2ColumnComparison<T>( ValueComparisonType.GE, col, other);
    }

    /**
	 * This method creates a condition filtering only those rows whose value in column col matches the pattern of the parameter value.
	 * Pattern matching is case sensitive.
	 */
    @Nonnull
	public static <T> BooleanExpression like(final AbstractColumnDef<T> col, final T value) {
		return new ColumnComparisonOperation<T>( ValueComparisonType.LIKE, col, value);
	}

	/**
	 * This method creates a condition filtering only those rows whose value in column col matches the pattern of the parameter value.
	 * Pattern matching is case insensitive.
	 */
    @Nonnull
	public static <T> BooleanExpression ilike(final AbstractColumnDef<T> col, final T value) {
		return new ColumnComparisonOperation<T>( ValueComparisonType.ILIKE, col, value);
	}

	/**
	 * Set the value of column col to the parameter value.
	 *
	 * @param col the column to set
	 * @param value the value to set
	 * @return the assignment
	 */
    @Nonnull
	public static <T> ColumnValueAssignment<T> set(final AbstractColumnDef<T> col, @Nullable final T value) {
		return new ColumnValueAssignmentImpl<T>( col, value);
	}

	/**
	 * Set the value of column col to the value of another column.
	 *
	 * @param col the column to set
	 * @param other the column providing the value
	 * @return the assignment
	 */
    @Nonnull
	public static <T> Column2ColumnAssignment<T> set(final AbstractColumnDef<T> col, final AbstractColumnDef<T> other) {
		return new Column2ColumnAssignmentImpl<T>( col, other);
	}

    /**
     * Get a unique-version of the select list item
     */
    public static <T> SelectListItem<T> distinct(final SelectListItem<T> col) {
        return new Aggregate<T>(AggregateFn.DISTINCT, col);
    }

	/**
	 * This method creates a condition filtering only those rows whose value is not null.
	 */
    @Nonnull
	public static <T> BooleanExpression isNotNull(final AbstractColumnDef<T> col) {
		return new IsNotNull( col);
	}

	/**
	 * This method creates a condition filtering only those rows whose value is null.
	 */
    @Nonnull
	public static <T> BooleanExpression isNull(final AbstractColumnDef<T> col) {
		return new IsNull( col);
	}

	/**
	 * This method creates a condition filtering only those rows whose value equals one of the given values.
	 */
	public static <V> BooleanExpression in( final SelectListItem<V> col, final V... values) {
		return new InValuesExpression<V>( col, Arrays.asList( values));
	}

	/**
	 * This method creates a condition filtering only those rows whose value is contained in the parameter values.
	 */
	@Nonnull
	public static <V> BooleanExpression in(final SelectListItem<V> col, final Iterable<V> values) {
		return new InValuesExpression<V>( col, values);
	}

	/**
	 * This method creates a condition filtering only those rows whose value is contained in the result of the subquery.
	 */
	@Nonnull
	public static <V> BooleanExpression in(final SelectListItem<V> col, final SubQuery<V> subquery) {

		return new InSubselectExpression<V>( col, subquery);
	}

	/**
	 * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
	 */
	public static class IsNull implements BooleanExpression {

		@Nonnull private final AbstractColumnDef<?> _col;

		IsNull(final AbstractColumnDef<?> col) {
			super();
			_col= col;
		}

		@Nonnull
		public AbstractColumnDef<?> getCol() {
			return _col;
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class IsNotNull implements BooleanExpression {

		@Nonnull private final AbstractColumnDef<?> _col;

		IsNotNull(final AbstractColumnDef<?> col) {
			super();
			_col= col;
		}

		@Nonnull
		public AbstractColumnDef<?> getCol() {
			return _col;
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class ColumnEquals<V> extends ColumnComparison<V> {

		ColumnEquals(final SelectListItem<V> col, @Nullable final V value) {
			super( ValueComparisonType.EQ, col, value);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
    public static class ColumnEqualsIgnoreCase<V> extends ColumnComparison<V> {

        ColumnEqualsIgnoreCase(final SelectListItem<V> col, @Nullable final V value) {
            super( ValueComparisonType.IEQ, col, value);
        }

        @Override
        public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
            return visitor.visit( this);
        }

    }

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class ColumnLessThan<V> extends ColumnComparison<V> {

		ColumnLessThan(final SelectListItem<V> col, final V value) {
			super( ValueComparisonType.LT, col, value);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class ColumnGreaterThan<V> extends ColumnComparison<V> {

		ColumnGreaterThan(final SelectListItem<V> col, final V value) {
			super( ValueComparisonType.GT, col, value);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class ColumnComparisonOperation<V> extends ColumnComparison<V> {

		protected ColumnComparisonOperation(final ValueComparisonType op, final AbstractColumnDef<V> col, final V value) {
			super( op, col, value);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}
	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class InValuesExpression<V> implements BooleanExpression {

		@Nonnull private final SelectListItem<V> _col;
		@Nonnull private final Iterable<V> _values;

		public InValuesExpression(final SelectListItem<V> col, final Iterable<V> values) {
			_col= col;
			_values= values;
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

		@Nonnull
		public SelectListItem<V> getColumn() {
			return _col;
		}

		@Nonnull
		public Iterable<V> getValues() {
			return _values;
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class InSubselectExpression<V> implements BooleanExpression {

		@Nonnull private final SelectListItem<V> _col;
		@Nonnull private final SubQuery<V> _subquery;

		public InSubselectExpression(final SelectListItem<V> col, final SubQuery<V> subquery) {
			_col= col;
			_subquery= subquery;
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

		@Nonnull
		public SubQuery<V> getSubquery() {
			return _subquery;
		}

		@Nonnull
		public SelectListItem<V> getColumn() {
			return _col;
		}
	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static abstract class BooleanCombination implements BooleanExpression {

		@Nonnull private final List<BooleanExpression> _expr= new ArrayList<BooleanExpression>();

		public BooleanCombination(final BooleanExpression... expressions) {
			for( final BooleanExpression booleanExpression : expressions) {
				_expr.add( booleanExpression);
			}
		}

		public BooleanCombination(final Iterable<BooleanExpression> expressions) {
			for( final BooleanExpression booleanExpression : expressions) {
				_expr.add( booleanExpression);
			}
		}

		@Nonnull
		public List<BooleanExpression> getExpr() {
			return _expr;
		}

		void addExpr(final BooleanExpression expr) {
			_expr.add( expr);
		}
	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Not implements BooleanExpression {

		@Nonnull private final BooleanExpression _expression;

		public Not(final BooleanExpression delegate) {
			_expression= delegate;
		}

		@Nonnull
		public BooleanExpression getExpression() {
			return this._expression;
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class And extends BooleanCombination {

		public And(final BooleanExpression... ex) {
			super( ex);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Or extends BooleanCombination {

		public Or(final BooleanExpression... ex) {
			super( ex);
		}

		@Override
		public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
			return visitor.visit( this);
		}

	}

	// FIXME (CM, KK, JK): https://github.com/greenhornet/freiheit_sqlapi/issues/5
	//        Jörg, deine Abfragefunktion aus dem Result-Set ist nicht optimal:
	//        du hast keine Kontrolle darüber, welcher Typ zurückkommt und musst raten!
	//        Das führt dazu, dass z.b. beim Count nicht - wie von dir beabsichtigt -
	//        ein Integer zurück kommt, sondern irgendwas anderes. Ich hatte gerade einen Long.
    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Count<T> extends Aggregate2<Long,T> implements NonnullSelectListItem<Long> {

		Count(final SelectListItem<T> item) {
			super( AggregateFn.COUNT, item, new DbType.DbLong());
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Max<T> extends Aggregate<T> {

		Max(final SelectListItem<T> item) {
			super( AggregateFn.MAX, item);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Min<T> extends Aggregate<T> {

		Min(final SelectListItem<T> item) {
			super( AggregateFn.MIN, item);
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Avg<T> extends Aggregate2<Float,T> {

		Avg(final SelectListItem<T> item) {
			super( AggregateFn.AVG, item, new DbType.DbFloat());
		}

	}

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Sum<T> extends Aggregate2<Long,T> {

		public Sum(final SelectListItem<T> item) {
			super( AggregateFn.SUM, item, new DbType.DbLong());
		}

	}


    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
    public static class Random implements SelectListItem<Float> {
        private static final DbType.DbFloat _type = new DbType.DbFloat();

        @Override
        public String fqName() {
            return "random()";
        }

        @Override
        public String name() {
            return "random()";
        }

        @Override
        public boolean isColumnName() {
            return false;
        }

        @Override
        public DbType<Float> type() {
            return _type;
        }

        @Override
        public <I> I accept(@Nonnull final OrderItemVisitor<I> visitor) {
            return visitor.visit(this);
        }
    }

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Aggregate2<T,V> implements SelectListItem<T> {

	    /*
	     * FIXME (CM): https://github.com/greenhornet/freiheit_sqlapi/issues/6
	     * das ist jetzt hier ein bischen reingeschummelt ;-)
	     * man muss das Token "count" aus dem Dialekt holen
	     */

	    @Nonnull private final AggregateFn _agg;
		@Nonnull private final SelectListItem<V> _item;
		@Nonnull private final String _name;
		@Nonnull private final String _fqName;
		@Nonnull private final DbType<T> _type;

		public Aggregate2(final AggregateFn agg, final SelectListItem<V> item, final DbType<T> type) {
			_agg= agg;
			_item= item;
			_type= type;
			_name= agg.getToken() + "(" + item.name() + ")";
			_fqName= agg.getToken() + "(" + item.fqName() + ")";
		}

		@Nonnull
		public AggregateFn getAgg() {
			return _agg;
		}

		@Nonnull
		public SelectListItem<V> getItem() {
			return _item;
		}

		@Override
		public String fqName() {
			return _fqName;
		}

		@Override
		public String name() {
			return _name;
		}

		@Override
		public boolean isColumnName() {
			return false;
		}

		@Override
		public DbType<T> type() {
			return _type;
		}

        @Override
        public <I> I accept(@Nonnull final OrderItemVisitor<I> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * This class is not part of the public API. Do not call it unless you know exactly what you are doing.
     */
	public static class Aggregate<T> extends Aggregate2<T,T> {

		public Aggregate(final AggregateFn agg, final SelectListItem<T> item) {
			super( agg, item, item.type());
		}

	}

	/**
	 * This method returns an exression for the aggregated count of another expression.
	 */
	@Nonnull
	public static <T> NonnullSelectListItem<Long> count(final SelectListItem<T> item) {
	    // TODO: https://github.com/greenhornet/freiheit_sqlapi/issues/7
	    // muss eine Liste von SelectListItems bekommen können
		return new Count<T>( item);
	}

	/**
	 * This method returns an exression for the aggregated maximum of another expression.
	 */
	@Nonnull
	public static <T> SelectListItem<T> max(final SelectListItem<T> item) {
		return new Max<T>( item);
	}

	/**
	 * This method returns an exression for the aggregated minimum of another expression.
	 */
	@Nonnull
	public static <T> SelectListItem<T> min(final SelectListItem<T> item) {
		return new Min<T>( item);
	}

	/**
	 * This method returns an exression for the aggregated average (arithmetic mean) of another expression.
	 */
	@Nonnull
	public static <T> SelectListItem<Float> avg(final SelectListItem<T> item) {
		return new Avg<T>( item);
	}

	/**
	 * This method returns an exression for the aggregated sum of another expression.
	 */
	@Nonnull
	public static <T> SelectListItem<Long> sum(final SelectListItem<T> item) {
		return new Sum<T>( item);
	}

	public static class Ordering<T> implements NullOrderItem {

        private final SelectListItem<T> _item;
        private final Direction _direction;
        private final NullOrder _nullOrder;

        Ordering(final SelectListItem<T> item, final Direction direction, @Nullable final NullOrder nullOrder) {
            _item = item;
            _direction = direction;
            _nullOrder = nullOrder;
        }

        @Override
        public OrderItem nullsFirst() {
            return new Ordering<T>(_item, _direction, NullOrder.NULLS_FIRST);
        }

        @Override
        public OrderItem nullsLast() {
            return new Ordering<T>(_item, _direction, NullOrder.NULLS_LAST);
        }

        public SelectListItem<T> getItem() {
            return _item;
        }

        public Direction getDirection() {
            return _direction;
        }

        @CheckForNull
        public NullOrder getNullOrder() {
            return _nullOrder;
        }

        @Override
        public <I> I accept(@Nonnull final OrderItemVisitor<I> visitor) {
            return visitor.visit(this);
        }
    }

	public static SelectListItem<Float> random() {
	    return new Random();
	}

	/**
	 * This method decorates its parameter expression for ascending ordering, suitable for use in an ORDER BY clauses.
	 */
	@Nonnull
	public static <T> NullOrderItem asc(final SelectListItem<T> item) {
		return new Ordering<T>( item, OrderItem.Direction.ASC, null );
	}

	/**
	 * This method decorates its parameter expression for descending ordering, suitable for use in an ORDER BY clauses.
	 */
	@Nonnull
	public static <T> NullOrderItem desc(final SelectListItem<T> item) {
		return new Ordering<T>( item, OrderItem.Direction.DESC, null );
	}

    public static final class ParameterPair<T1,T2> {
        @CheckForNull
        private final T1 _left;
        @CheckForNull
        private final T2 _right;

        private ParameterPair(@Nullable final T1 left,@Nullable final  T2 right) {
            super();
            _left = left;
            _right = right;
        }

        public T1 getLeft() {
            return _left;
        }

        public T2 getRight() {
            return _right;
        }

        public static <T1,T2> ParameterPair<T1,T2> of(@Nullable final T1 left,@Nullable final T2 right) {
            return new ParameterPair<T1,T2>(left, right);
        }

    }

    public final static class PairInExpression<T1, T2> implements BooleanExpression {

        private final Collection<ParameterPair<T1, T2>> _parameterPairs;
        private final ColumnDef<T1> _column1;
        private final ColumnDef<T2> _column2;

        /**
         * Konstruktor.
         */
        private PairInExpression(
                final Collection<ParameterPair<T1, T2>> parameterPairs,
                final ColumnDef<T1> column1, final ColumnDef<T2> column2) {
                    _parameterPairs = parameterPairs;
                    _column1 = column1;
                    _column2 = column2;
        }

        @Override
        public <T> T accept(final BooleanExpressionVisitor<T> visitor) {
	        return visitor.visit(this);
        }

        public Collection<ParameterPair<T1, T2>> getParameterPairs() {
            return _parameterPairs;
        }

        public ColumnDef<T1> getColumn1() {
            return _column1;
        }

        public ColumnDef<T2> getColumn2() {
            return _column2;
        }

    }

    public static <T1,T2> BooleanExpression pairIn(final Collection<ParameterPair<T1,T2>> parameterPairs, final ColumnDef<T1> column1, final ColumnDef<T2> column2) {
        return new PairInExpression<T1,T2>(parameterPairs,column1, column2 );
    }

}
