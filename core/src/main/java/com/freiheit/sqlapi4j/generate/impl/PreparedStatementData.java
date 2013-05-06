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
import java.util.LinkedList;
import java.util.List;

public class PreparedStatementData {
	public static final PreparedStatementData NO_PREPARED_STATEMENT= new PreparedStatementData( false);

	private final boolean _preparedStatement;
	private final List<Object> _paramValues= new LinkedList<Object>();

	public PreparedStatementData( boolean preparedStatement) {
		_preparedStatement= preparedStatement;
	}

	public void collectValue( Object value) {
		_paramValues.add( value);
	}

	public Iterator<?> getValues() {
		return _paramValues.iterator();
	}

	public boolean isPreparedStatement() {
		return _preparedStatement;
	}

	@Override
	public String toString() {
		return _paramValues.toString();
	}

}
