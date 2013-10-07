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

import java.util.Deque;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports execution of non-transactional code.
 *
 * <p>This class is mutable and must only be used thread-confined.</p>
 *
 * <p><b>WARNING:</b> This implementation is currently not size-limited and does not
 * serialize the Runnables to disk. In other words: if you keep your transactions open for a long
 * time, you may run out of memory! Be careful about the amount of work you delay with this class.</p>
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 *
 */
public class NonTransactionalSupport {
    private static final Logger NON_TRANSACTIONAL_LOG = LoggerFactory.getLogger("NON_TRANSACTIONAL");

    private final Deque<Runnable> _deque = new LinkedList<Runnable>();

    private boolean _inTransaction;

    private enum NonTranscationalLogType {
        ENQUEUE,
        DIRECT,
        BEFORE_EXECUTION,
        AFTER_EXECUTION_SUCCESS,
        AFTER_EXECUTION_FAIL;
    }

    /**
     * If there is a transaction currently running, the runnable will be enqueued for
     * execution after the successfull transaction commit. Else it will be executed
     * immediately.
     *
     * @param r the runnable - should implement #toString() in a meaningful way to help recover if the server should die
     * after the actual commit and before the execution of the queue.
     */
    public void execute(@Nonnull final Runnable r) {

        if (_inTransaction) {
            log(NonTranscationalLogType.ENQUEUE, r);
            _deque.addLast(r);
        } else {
            log(NonTranscationalLogType.DIRECT, r);
            runNonTransactional(r);
        }

    }

    private void log(@Nonnull final NonTranscationalLogType type, @Nonnull final Runnable runnable) {
        NON_TRANSACTIONAL_LOG.info(type + " | " + runnable);
    }

    private void runNonTransactional(@Nonnull final Runnable runnable) {
        log(NonTranscationalLogType.BEFORE_EXECUTION, runnable);
        try {
            runnable.run();
            log(NonTranscationalLogType.AFTER_EXECUTION_SUCCESS, runnable);
            //CSOFF:IllegalCatch
        } catch (final Throwable t) {
            //CSON:IllegalCatch
            log(NonTranscationalLogType.AFTER_EXECUTION_FAIL, runnable);
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new IllegalStateException(t);
        }
    }

    /**
     * Call this to mark a transaction as started - will cause all runnables to be queued for execution after successsfull commit.
     */
    public void transactionStarted() {
        _inTransaction = true;
    }

    /**
     * Exeucte all runnables registered previously.
     */
    public void transactionCommitted() {
        try {
            // if any of the runnables should lead to a call back into this class, we
            // need to exeucte the calls directly again.
            _inTransaction = false;
            for (final Runnable r : _deque) {
                runNonTransactional(r);
            }
        } finally {
            _deque.clear();
        }
    }

    /**
     * Call this in a finally-block after the execution of whatever you are doing
     * to clean up the state of this NonTransactionalSupport.
     *
     * This should be called independent of whether the transaction was or wasn't
     * commited.
     */
    public void cleanUp() {
        _deque.clear();
    }
}
