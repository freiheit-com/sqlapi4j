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

import com.freiheit.sqlapi4j.query.FromDef;
import com.freiheit.sqlapi4j.query.impl.ValueComparisonType;

public class H2DbDialect extends BasicSqlDialect {

    public H2DbDialect( ConverterRegistry parent) {
        super( parent);
    }

    @Override
    public void sequenceNextval( StringBuilder sb, String seqName) {
        sb.append( "NEXTVAL('");
        sb.append( seqName);
        sb.append( "')");
    }

    @Override
    public void addComparison( StringBuilder sb, ValueComparisonType cmpType, String colFqName, String quotedConvertedValue) {
        switch( cmpType) {
        case ILIKE:
            sb.append( "UPPER(" + colFqName + ")");
            addValueComp( sb, ValueComparisonType.LIKE);
            sb.append( "UPPER(" + quotedConvertedValue + ")");
            break;
        default:
            super.addComparison( sb, cmpType, colFqName, quotedConvertedValue);
        }
    }

    @Override
    public String sequenceNextvalCmd( String sequenceName ) {
        return "select NEXTVAL('" + sequenceName + "') as value";
    }

    @Override
    public void addIndexToUse(StringBuilder sb, String indexName,
            FromDef ... fromDefs) {
        // Unterstützt H2 nicht.
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return false;
    }


}
