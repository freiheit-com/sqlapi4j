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
package com.freiheit.sqlapi.query;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import com.freiheit.sqlapi.query.statements.CreateTableStatement;
import com.freiheit.sqlapi.query.statements.DeleteStatement;
import com.freiheit.sqlapi.query.statements.InsertStatement;
import com.freiheit.sqlapi.query.statements.SelectSequenceStatement;
import com.freiheit.sqlapi.query.statements.SelectStatement;
import com.freiheit.sqlapi.query.statements.UpdateStatement;

/**
 * Execution of SQL Statements.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public interface SqlExecutor {
    SelectResult execute(@Nonnull Connection connection, @Nonnull SelectStatement statement) throws SQLException;
    String render(@Nonnull SelectStatement statement);
    int execute(@Nonnull Connection connection, @Nonnull DeleteStatement statement) throws SQLException;
    String render(@Nonnull DeleteStatement statement);
    int execute(@Nonnull Connection connection, @Nonnull InsertStatement statement) throws SQLException;
    String render(@Nonnull InsertStatement statement) throws SQLException;
    int execute(@Nonnull Connection connection, @Nonnull UpdateStatement statement) throws SQLException;
    String render(@Nonnull UpdateStatement statement);
    /**
     * Execute the sequence command and convert the result to the java type of the specified targetColumn.
     * Note that the Db-Type of that column needs to be equal to the Db-Type of the Sequence, which is typically Long.
     */
    <T> T execute(@Nonnull Connection connection, @Nonnull SelectSequenceStatement statement, SelectListItem<T> targetColumn) throws SQLException;
    long execute(@Nonnull Connection connection, @Nonnull SelectSequenceStatement statement) throws SQLException;
    String render(@Nonnull SelectSequenceStatement statement) throws SQLException;
    void execute(@Nonnull Connection connection, @Nonnull CreateTableStatement statement) throws SQLException;
    String render(@Nonnull CreateTableStatement statement) throws SQLException;
}
