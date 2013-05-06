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
package com.freiheit.sqlapi.query.impl;


import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.freiheit.sqlapi.query.BooleanExpression;
import com.freiheit.sqlapi.query.FromDef;
import com.freiheit.sqlapi.query.SelectListItem;
import com.freiheit.sqlapi.query.clause.LockMode;
import com.freiheit.sqlapi.query.statements.SelectStatement;

public class SqlAst implements SelectStatement {

	private SelectListItem<?>[] _selectItems;
	private FromDef[] _fromDef;
	private BooleanExpression _where;
	private SelectListItem<?>[] _groupItems;
	private SelectListItem<?>[] _orderItems;
	private Integer _limitNum;
	private Integer _offsetNum;
	private LockMode _lockMode;
    private BooleanExpression _having;
    private String _indexName;

	public SqlAst() {
	}

	/**
	 * Copy-Constructor: Copy values from the original.
	 * Attention: currently this is a shallow copy - you are *not* supposed to alter any of the arrays.
	 */
	public SqlAst(@Nonnull final SelectStatement original) {
	    _selectItems = original.getSelectItems();
	    _fromDef = original.getFromDef();
	    _where = original.getWhere();
	    _groupItems = original.getGroupItems();
	    _orderItems = original.getOrderItems();
	    _limitNum = original.getLimitNum();
	    _offsetNum = original.getOffsetNum();
	    _lockMode = original.getLockMode();
	    _having = original.getHaving();
	    _indexName = original.getIndexName();
	}

	public SqlAst( final FromDef[] fromDef, final BooleanExpression where, final SelectListItem<?>... selectItems) {
		_selectItems= selectItems;
		_fromDef= fromDef;
		_where= where;
	}

	@Override
    public SelectListItem<?>[] getSelectItems() {
		return _selectItems;
	}

	public void setSelectItems( final SelectListItem<?>... selectItems) {
		_selectItems= selectItems;
	}

	@Override
    public FromDef[] getFromDef() {
		return _fromDef;
	}

	public void setFromDef( final FromDef... fromDef) {
		_fromDef= fromDef;
	}

	@Override
    public BooleanExpression getWhere() {
		return _where;
	}

	public void setWhere( final BooleanExpression where) {
		_where= where;
	}

	@Override
    public SelectListItem<?>[] getOrderItems() {
		return _orderItems;
	}

	public void setOrderItems( final SelectListItem<?>... orderItems) {
		// TODO: https://github.com/greenhornet/freiheit_sqlapi/issues/9
	    // order direction????
		_orderItems= orderItems;
	}

	@Override
    public SelectListItem<?>[] getGroupItems() {
		return _groupItems;
	}

	public void setGroupItems( final SelectListItem<?>... items) {
		_groupItems= items;
	}

	public void setLimitNum( final Integer limitNum) {
		_limitNum= limitNum;
	}

	@Override
    public Integer getLimitNum() {
		return _limitNum;
	}

	public void setOffsetNum( final Integer offsetNum) {
		_offsetNum= offsetNum;
	}

	@Override
    public Integer getOffsetNum() {
		return _offsetNum;
	}

    public void setLockMode( final LockMode lockMode ) {
        _lockMode= lockMode;
    }

    @Override
    public LockMode getLockMode() {
        return _lockMode;
    }

    public void setHaving(final BooleanExpression conditions) {
        _having = conditions;
    }

    @Override
    public BooleanExpression getHaving() {
        return _having;
    }

    @Override
    @CheckForNull
    public String getIndexName() {
        return _indexName;
    }

    public void setIndexName(@Nullable final String indexName) {
        _indexName = indexName;
    }

}
