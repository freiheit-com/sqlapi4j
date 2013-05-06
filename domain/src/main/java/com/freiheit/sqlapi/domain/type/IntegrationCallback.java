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

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Encapsulate the commands to retrieve data from the database
 *
 * @param <T> the type of the returned object. Use {@link Void} if you don't wish to return a thing.
 *
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
@ParametersAreNonnullByDefault
public interface IntegrationCallback<T> {

    /**
     * Execute some Sql-Commands.
     * The given connection can be used immediately and will be closed afterwards.
     */
    @CheckForNull
    public T execute(final Connection connection) throws SQLException;

}
