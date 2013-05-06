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
package com.freiheit.sqlapi.domain.type;

import java.sql.SQLException;

import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.domain.exception.DataAccessException;


/**
 * Creates and closes the database connection around the execution.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
@ParametersAreNonnullByDefault
public interface TransactionTemplate {

    /**
     * Execute the given callback and take care of the connection before and afterwards.
     * If a {@link RuntimeException} or an {@link SQLException} is thrown out of the
     * callback, the transaction will be rolled back.
     *
     * @param <T> the return type of the given callback
     * @param callback the callback that performs the actual request
     * @return whatever the callback returns
     */
    <T> T execute(final IntegrationCallback<T> callback) throws DataAccessException;

}
