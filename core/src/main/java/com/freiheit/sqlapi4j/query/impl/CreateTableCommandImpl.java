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


import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.CreateTableCommand;
import com.freiheit.sqlapi4j.query.statements.CreateTableStatement;

public class CreateTableCommandImpl implements CreateTableCommand {

	private final TableDef _table;

	public CreateTableCommandImpl(final TableDef table) {
		_table= table;
	}

	@Override
    public CreateTableStatement stmt() {
	    return new CreateTableStatementImpl(_table);
	}

	@Override
	public String toString() {
		return stmt().toString();
	}

}
