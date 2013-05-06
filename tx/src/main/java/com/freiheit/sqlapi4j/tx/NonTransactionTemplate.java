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
 * Like {@link com.freiheit.sqlapi4j.domain.type.TransactionTemplate}, but for
 * code that does not support transaction semantics and thus must run outside of
 * the transaction scope.
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
public interface NonTransactionTemplate {

    /**
     * Execute the given runnable after successfull commit of the current
     * transaction, or immediately if there is no transaction currently running.
     *
     * <p>
     * Use this to ensure that your non-transactional code will only be executed
     * for successfull transactions. It is intended for things like sending
     * email or logging changes.
     * </p>
     *
     *
     * <p>
     * If the server should die right after the code is executed but before
     * execution of your runnable, you will loose the execution of that
     * runnable. The implementation of this interface will log that you
     * requested to execute this runnable - so if you implement toString() of
     * {@link Runnable} appropriately, you might be able to recover
     * by hand, or at least prove that the runnable was (or was not) executed.
     * </p>
     *
     * <p>
     * Multiple calls to this function will be executed in order after a
     * successfull commit. Thus, if you first call this to log "data change a"
     * and then again for "data change b", the actual logging calls will be done
     * in the correct order.
     * </p>
     *
     * <p>
     * <b>Note:</b> If your runnable throws an Exception or Error, the
     * transaction will still have been committed, but runnables that were
     * registered for subsequent execution will not be executed any more. Your
     * caller will receive the throwable thrown by your runnable after the
     * commit of the outermost transaction.
     * </p>
     *
     * <p>
     * It is recommended to prepare your execution outside of the runnable,
     * especially if you need your transaction to be rolled back on certain
     * errors, and to only execute the code that cannot be rolled back within
     * your runnable.
     * </p>
     *
     * @param runnable the runnable to execute.
     */
    void executeAfterCommit( @Nonnull Runnable runnable );

    /**
     * Executes the given runnable if there is currently no transaction in
     * progress, else fails.
     *
     * @throws IllegalStateException
     *             if there is currently a transaction, else executes the
     *             runnable. Will also throw this exception, if the runnable
     *             throws a checked exception.
     * @param <T>
     *            the result of the callable
     */
    @CheckForNull
    <T> T executeWithoutTransaction( @Nonnull Callable<T> runnable ) throws IllegalStateException;
}
