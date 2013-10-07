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

import com.freiheit.sqlapi4j.tx.EnhancedTransactionTemplate;
import com.freiheit.sqlapi4j.tx.TransactionInterceptor;
import com.freiheit.sqlapi4j.tx.Transactional;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.matcher.Matchers;

final class TransactionInterceptorModule extends AbstractModule {
    @Override
    public void configure() {
        final TransactionInterceptor transactionInterceptor = new TransactionInterceptor() {
            @Inject
            @Override
            public void setTransactionTemplate(final EnhancedTransactionTemplate transactionTemplate) {
                super.setTransactionTemplate(transactionTemplate);
            }
        };
        requestInjection(transactionInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionInterceptor);
    }
}