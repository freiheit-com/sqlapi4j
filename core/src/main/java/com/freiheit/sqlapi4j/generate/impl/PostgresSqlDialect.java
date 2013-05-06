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

import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.clause.LockMode;



public class PostgresSqlDialect extends BasicSqlDialect {

	public PostgresSqlDialect( ConverterRegistry parent) {
		super( parent);
	}

	@Override
	public void sequenceNextval( StringBuilder sb, String seqName) {
		sb.append( "nextval('");
		sb.append( seqName);
		sb.append( "')");
	}

	@Override
	public String sequenceNextvalCmd( String sequenceName ) {
		return "select nextval('"+sequenceName+"') as value";
	}
	
	@Override
	public String addLockMode(LockMode lockMode) {
		switch (lockMode) {
		case UPGRADE:
		case UPGRADE_SKIP_LOCKED:
			return " for update";
		case UPGRADE_NO_WAIT:
			return " for update nowait";
	}
	return "";
	}

	@Override
    public void addIndexToUse(StringBuilder sb, String indexName,
            FromDef ... fromDefs) {
	    // Unterstützt Postgres nicht.
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

}
