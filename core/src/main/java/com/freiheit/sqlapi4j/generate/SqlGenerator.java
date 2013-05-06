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
package com.freiheit.sqlapi4j.generate;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.generate.impl.PreparedStatementData;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.BooleanExpression;
import com.freiheit.sqlapi4j.query.ColumnAssignment;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.Sql.BooleanCombination;
import com.freiheit.sqlapi4j.query.impl.ResultFlags;
import com.freiheit.sqlapi4j.query.statements.SelectStatement;

@ParametersAreNonnullByDefault
public interface SqlGenerator {

    //static SqlGenerator DEFAULT_GENERATOR= new SqlGeneratorImpl();

    @Nonnull
    String generateQueryString( SqlDialect dialect, SelectStatement abstractSyntax, ResultFlags resFlags, PreparedStatementData preparedStatementData);

    @Nonnull
    String generatePreparedStatement( SqlDialect dialect, SelectStatement abstractSyntax);

    @Nonnull
    String generateInsertStatement( SqlDialect dialect, TableDef table, List<ColumnAssignment<?>[]> assignments, @Nullable FromDef[] fromDefs, @Nullable BooleanExpression booleanExpressions, PreparedStatementData preparedStatementData);

    @Nonnull
    String generateUpdateStatement( SqlDialect dialect, TableDef table, ColumnAssignment<?>[] assignments, @Nullable BooleanCombination boolComb, PreparedStatementData preparedStatementData);

    @Nonnull
    String generateDeleteStatement( SqlDialect dialect, TableDef table, @Nullable BooleanCombination boolComb, PreparedStatementData preparedStatementData);

    @Nonnull
    String generateCreateTableStatement( SqlDialect dialect, TableDef table);

    @Nonnull
    String generateSequenceNextvalCmd( SqlDialect dialect, String sequenceName);

}
