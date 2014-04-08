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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi4j.meta.ColumnConverter;
import com.freiheit.sqlapi4j.meta.DbType;
import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.clause.LockMode;
import com.freiheit.sqlapi4j.query.impl.ValueComparisonType;

@ParametersAreNonnullByDefault
public interface SqlDialect {

    void addQuote( StringBuilder sb);

    void addSelectListSeparator( StringBuilder sb);

    void addOpenParenthese( StringBuilder sb);

    void addCloseParenthese( StringBuilder sb);

    void addNot( StringBuilder sb);

    void addAnd( StringBuilder sb);

    void addOr( StringBuilder sb);

    void addPreparedStatementPar( StringBuilder sb);

    void addIsNull( StringBuilder sb);

    void addIsNotNull( StringBuilder sb);

    void addWhere( StringBuilder sb);

    void addFrom( StringBuilder sb);

    void addSelect( StringBuilder sb);

    <T> boolean needsQuote( DbType<T> dbType);

    @CheckForNull
    <T> ColumnConverter<T,?> getConverterFor(@Nullable Class<? extends DbType<T>> dbType);

    @Nonnull
    SqlStdConverter getStandardConverter();

    void addOrderBy( StringBuilder sb);

    void addGroupBy( StringBuilder sb);

    void addInsert( StringBuilder sb);

    void addValues( StringBuilder sb);

    void addUpdate( StringBuilder sb);

    void addDelete( StringBuilder sb);

    void addSet( StringBuilder sb);

    void addAssign( StringBuilder sb);

    void addLimitAndOffset(StringBuilder sb,@Nullable Integer offsetNum,@Nullable Integer limitNum);

    void addIn( StringBuilder sb);

    void addCreateTable( StringBuilder sb);

    void sequenceNextval( StringBuilder sb, String seqName);

    void addComparison( StringBuilder sb, ValueComparisonType cmpType, String colFqName, String quotedConvertedValue);

    String sequenceNextvalCmd( String sequenceName );

    String addLockMode(LockMode lockMode);

    void addHaving(StringBuilder sb);

    void addIndexToUse(StringBuilder sb, String indexName, FromDef ... fromDefs);

    /**
     * Does this dbms support multiple column in-queries?
     */
    boolean supportsRowValueConstructorSyntaxInInList();

    void addJoinOn(@Nonnull StringBuilder sb);

    void addLeftOuterJoin(@Nonnull StringBuilder sb);

    void addRightOuterJoin(@Nonnull StringBuilder sb);

    void addFullOuterJoin(@Nonnull StringBuilder sb);

    void addInnerJoin(@Nonnull StringBuilder sb);
}
