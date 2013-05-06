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
package com.freiheit.sqlapi.query.executors;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Decouples execution of a command from the actual statement state, so that common code may add information to the statement.
 * Needed for id generation, automatic insertion of createdAt and modifiedBy etc.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 * @param <T> the type of the state instance for the command
 * @param <R> the result of executing the command
 */
public interface SqlStatementExecutor<T, R> {
    /**
     * Executes the given Statement on the specified connection.
     */
    R execute(Connection connection, T statement) throws SQLException;

    /**
     * Returns the string representation of the given statment.
     */
    String toString(T statement);
}
