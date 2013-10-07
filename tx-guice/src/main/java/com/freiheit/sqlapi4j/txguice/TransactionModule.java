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
package com.freiheit.sqlapi4j.txguice;

import javax.sql.DataSource;

import com.freiheit.sqlapi4j.domain.type.TransactionTemplate;
import com.freiheit.sqlapi4j.tx.EnhancedTransactionTemplate;
import com.freiheit.sqlapi4j.tx.NonTransactionTemplate;
import com.freiheit.sqlapi4j.tx.TransactionTemplateImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Module for configuring database transactions.
 *
 * @author Andreas Baldeau (andreas.baldeau@freiheit.com)
 */
public class TransactionModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(TransactionTemplateImpl.class).toConstructor(TransactionTemplateImpl.class.getConstructor(DataSource.class)).in(Scopes.SINGLETON);
        } catch (final NoSuchMethodException e) {
            addError(e);
        }
        bind(TransactionTemplate.class).to(TransactionTemplateImpl.class).in(Scopes.SINGLETON);
        bind(NonTransactionTemplate.class).to(TransactionTemplateImpl.class).in(Scopes.SINGLETON);
        bind(EnhancedTransactionTemplate.class).to(TransactionTemplateImpl.class).in(Scopes.SINGLETON);

        install(new TransactionInterceptorModule());
    }
}
