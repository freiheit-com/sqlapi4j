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

import javax.annotation.Nonnull;

import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.clause.LockMode;

/**
 * SQL-API Dialect for Oracle 11g.
 * 
 * @author Fabian Loewner (fabian.loewner@freiheit.com)
 * 
 */
public class OracleSqlDialect extends BasicSqlDialect {

	public OracleSqlDialect( @Nonnull final ConverterRegistry parent ) {
		super( parent );
	}

	@Override
	public void sequenceNextval(final StringBuilder sb, final String sequenceName) {
		sb.append( sequenceName ).append( ".nextVal" );
	}

	@Override
	public String sequenceNextvalCmd(final String sequenceName) {
		return "select " + sequenceName + ".nextval as value from dual";
	}

	@Override
	public String addLockMode(LockMode lockMode) {
		switch(lockMode) {
			case UPGRADE:
				return " for update";
			case UPGRADE_NO_WAIT:
				return " for update nowait";
			case UPGRADE_SKIP_LOCKED:
				return " for update skip locked";
		}
		return "";
	}
	
	@Override
	public void addLimitAndOffset(StringBuilder sb, Integer offsetNum,
			Integer limitNum) {
		if (offsetNum==null && limitNum==null) {
			return;
		}
		if(offsetNum == null || offsetNum == 0) {
			// nur Limit: wir können einfach per rownum selektieren. 
			sb.insert(0, "select * from (");
			sb.append(") where rownum <=").append(limitNum);
			return;
		}
		// Offset (und Limit): Das wird häßlich, wir müssen zwei SQL-Statements schachteln.
		sb.insert(0, "select * from ( select row_.*, rownum rownum_ from (");
		sb.append(") row_ ) where ");
		if (limitNum != null && limitNum != 0) {
			sb.append( "rownum_ <=").append(offsetNum+limitNum).append( " and ");
		}
		sb.append("rownum_>").append(offsetNum);
	}

    @Override
    public void addIndexToUse(StringBuilder sb, String indexName, FromDef ... fromDefs) {
        if (indexName.contains("*/")) {
            throw new IllegalArgumentException("Index names may not contain comment-end-delimiters");
        }
        sb.append (" /*+ INDEX (").append(fromDefs[0].getTableName()).append(" ").append(indexName).append(")*/ ");
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

}
