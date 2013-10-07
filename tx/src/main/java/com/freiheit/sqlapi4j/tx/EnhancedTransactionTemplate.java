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
package com.freiheit.sqlapi4j.tx;

import java.util.concurrent.Callable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Transaction template that propagates a new transaction.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public interface EnhancedTransactionTemplate {

    /**
     * Executes the callback within a transaction, always creating a new Transaction and
     * never reusing a surrounding transaction.
     */
    @CheckForNull
    <T> T executePropagateNew(@Nonnull Callable<T> callback);

    /**
     * Executes the callback within a transaction, only creating a new Transaction if there is no surrounding transaction.
     */
    @CheckForNull
    <T> T execute(@Nonnull Callable<T> callback);
}
