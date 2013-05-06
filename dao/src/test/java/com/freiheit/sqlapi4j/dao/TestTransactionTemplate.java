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
package com.freiheit.sqlapi4j.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.freiheit.sqlapi4j.domain.exception.DataAccessException;
import com.freiheit.sqlapi4j.domain.type.IntegrationCallback;
import com.freiheit.sqlapi4j.domain.type.TransactionTemplate;
import com.freiheit.sqlapi4j.test.hsql.TestDb.DbOperation;


/**
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 *
 */
public class TestTransactionTemplate implements TransactionTemplate {

    @Override
    public <T> T execute(final IntegrationCallback<T> callback) throws DataAccessException {
        return DaoTestDb.INSTANCE.executeSingle(new DbOperation<T>() {

            @Override
            public T execute(final Connection c) throws SQLException {
                return callback.execute(c);
            }

        });
    }

}
