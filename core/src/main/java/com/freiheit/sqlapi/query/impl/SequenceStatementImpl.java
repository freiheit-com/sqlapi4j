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

import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.meta.SequenceDef;
import com.freiheit.sqlapi.query.statements.SelectSequenceStatement;

@ParametersAreNonnullByDefault
public class SequenceStatementImpl implements SelectSequenceStatement {

    private final SequenceDef _def;

    public SequenceStatementImpl(final SequenceDef def) {
        super();
        _def = def;
    }

    @Override
    public SequenceDef getSequence() {
        return _def;
    }
}
