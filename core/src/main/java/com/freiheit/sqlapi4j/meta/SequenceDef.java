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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Metadata for database sequences.
 */
@ParametersAreNonnullByDefault
public class SequenceDef {

	@Nonnull private final String _sequenceName;
	private final int _incrementBy;

	/**
	 * @param sequenceName the sequence name
	 * @param incrementBy the sequence's increment step
	 */
	public SequenceDef(final String sequenceName, final int incrementBy) {
		_sequenceName= sequenceName;
		_incrementBy= incrementBy;
	}

	/**
	 * Get the sequence name.
	 */
	@Nonnull
	public String getSequenceName() {
		return _sequenceName;
	}

	/**
	 * Get the sequence's increment step.
	 */
	public int getIncrementBy() {
		return _incrementBy;
	}

}
