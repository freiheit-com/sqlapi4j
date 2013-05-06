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
package com.freiheit.sqlapi.tx;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freiheit.sqlapi.domain.exception.DataAccessException;
import com.freiheit.sqlapi.domain.type.IntegrationCallback;
import com.freiheit.sqlapi.domain.type.TransactionTemplate;

/**
 * Default implementation of {@link TransactionTemplate}.
 * 
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 * @author Klas Kalass (klas.kalass@freiheit.com)
 * 
 */
public class TransactionTemplateImpl implements TransactionTemplate, NonTransactionTemplate {

    private static final Logger LOG = LoggerFactory.getLogger( TransactionTemplate.class );

    private static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<Connection>();
    private static final ThreadLocal<AtomicInteger> TRANSACTION_DEPTH = new ThreadLocal<AtomicInteger>() {

        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger();
        }
    };

    private static final ThreadLocal<NonTransactionalSupport> AFTER_TRANSACTION_RUNNABLES =
            new ThreadLocal<NonTransactionalSupport>() {
                @Override
                protected NonTransactionalSupport initialValue() {
                    return new NonTransactionalSupport();
                }
            };

    /**
     * Exception to wrap {@link SQLException} to support retry-behaviour.
     */
    private static final class RetryException extends Exception {
        public RetryException( @Nonnull final RuntimeException e ) {
            super( e );
        }

        @Override
        public synchronized RuntimeException getCause() {
            return (RuntimeException) super.getCause();
        }
    }

    private final DataSource _dataSource;

    public TransactionTemplateImpl( @Nonnull final DataSource dataSource ) {
        _dataSource = dataSource;
    }

    @Override
    public void executeAfterCommit( final Runnable runnable ) {
        AFTER_TRANSACTION_RUNNABLES.get().execute( runnable );
    }

    /**
     * Execute the given callback and take care of the connection before and
     * afterwards. If a {@link RuntimeException} or an {@link SQLException} is
     * thrown out of the callback, the transaction will be rolled back.
     * 
     * @param <T>
     *            the return type of the given executor
     * @param exec
     *            the executor that performs the actual request
     * @return whatever the executor returns
     */
    @Override
    public <T> T execute( final IntegrationCallback<T> exec ) throws DataAccessException {
        // * TODO(CM): https://github.com/greenhornet/freiheit_sqlapi/issues/14
        //hier solle eine configuration-bean für die connection benutzt werden.

        try {
            final int currentDepth = TRANSACTION_DEPTH.get().incrementAndGet();
            if ( currentDepth == 1 ) {
                return executeToplevelWithRetries( exec, 5 );
            } else {
                try {
                    final Connection connection = CONNECTION.get();
                    if (connection == null) {
                        throw new NullPointerException("connection is null");
                    }
                    return exec.execute( connection );
                } catch ( final SQLException e ) {
                    throw new DataAccessException( e );
                }
            }
        } finally {
            TRANSACTION_DEPTH.get().decrementAndGet();
        }

    }

    @CheckForNull
    private <T> T executeToplevelWithRetries( @Nonnull final IntegrationCallback<T> exec, final int max ) {
        int retries = 0;
        RetryException lastException = null;
        while ( retries < max ) {

            try {
                return doInTopLevelTransaction( exec );
            } catch ( final RetryException e ) {
                retries++;
                final StringBuilder sb = new StringBuilder();
                if ( retries < max ) {
                    sb.append( "Will retry for the " ).append( retries ).append( ". time" );
                } else {
                    sb.append( "Giving up on retrying after " ).append( retries ).append( "times" );
                }
                LOG.warn( sb.toString(), e.getCause() );
                lastException = e;
            }
        }
        if ( lastException == null ) {
            throw new IllegalStateException( "Got " + retries + " retries without ending a call, but no exception causing them" );
        }
        throw lastException.getCause();
    }

    @CheckForNull
    private <T> T doInTopLevelTransaction( @Nonnull final IntegrationCallback<T> exec ) throws RetryException {
        final Connection connection = getFreshConnection();
        CONNECTION.set( connection );
        try {
            AFTER_TRANSACTION_RUNNABLES.get().transactionStarted();
            // start transaction
            connection.setAutoCommit( false );
            // do stuff
            final T result = exec.execute( connection );
            // close transaction if at most top transaction
            connection.commit();
            AFTER_TRANSACTION_RUNNABLES.get().transactionCommitted();
            return result;
        } catch ( final SQLException e ) {
            rollBack( connection );
            throw new RetryException( new DataAccessException( e ) );

            // CSOFF:IllegalCatch
            // is turned off here,
            // since the transaction must be rolled back -
            // at least it is tried.
        } catch ( final RuntimeException e ) {
            // CSON:IllegalCatch

            // the rollback-method is not expected to throw exceptions,
            // and the original RuntimeException is rethrown afterwards.
            LOG.warn( "Caught RuntimeException. Will rollback and rethrow: " + e.getMessage(), e );
            try {
                rollBack( connection );
                //CSOFF:.
            } catch ( final RuntimeException e2 ) {
                //CSON:.
                LOG.warn( "Rollback RuntimeException:" + e2.getMessage(), e2 );
            }
            throw e;
        } finally {
            try {
                AFTER_TRANSACTION_RUNNABLES.get().cleanUp();
                getAndLogWarningsSilently( connection );
            } finally {
                closeConnectionSilently( connection );
                CONNECTION.remove();
            }
        }
    }

    @Nonnull
    private Connection getFreshConnection() {
        try {
            return _dataSource.getConnection();
        } catch ( final SQLException e ) {
            throw new DataAccessException( e );
        }
    }

    private void closeConnectionSilently( @Nonnull final Connection connection ) {
        try {
            connection.close();
        } catch ( final SQLException e ) {
            if ( LOG.isErrorEnabled() ) {
                LOG.error( "Exception while closing connection: ", e );
            }
        }
    }

    private void getAndLogWarningsSilently( @Nonnull final Connection connection ) {
        try {
            logWarnings( connection.getWarnings() );
        } catch ( final SQLException e ) {
            if ( LOG.isWarnEnabled() ) {
                LOG.warn( "Uncritical error: Cannot get warnings:", e );
            }
            return;
        }
    }

    private void logWarnings( @Nonnull final SQLWarning warning ) {
        SQLWarning localWarning = warning;
        while ( localWarning != null ) {
            if ( LOG.isWarnEnabled() ) {
                LOG.warn( "Warning while transaction:", localWarning );
            }

            localWarning = localWarning.getNextWarning();
        }
    }

    /**
     * Roll the transaction back ignoring possible exceptions.
     */
    private void rollBack( @Nonnull final Connection connection ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Rolling back." );
        }

        try {
            connection.rollback();
        } catch ( final SQLException e ) {
            // don't throw anything that would shadow the cause
            // why the transaction is rolled back in the first place.
            if ( LOG.isErrorEnabled() ) {
                LOG.error( "Cannot rollback transaction", e );
            }
        }
    }

    @Override
    public <T> T executeWithoutTransaction( final Callable<T> callable ) throws IllegalStateException {
        if ( TRANSACTION_DEPTH.get().intValue() == 0 ) {
            try {
                return callable.call();
                // CSOFF:IllegalCatch
            } catch ( final Exception e ) {
                //CSON:IllegalCatch
                throw new IllegalStateException( e );
            }
        } else {
            throw new IllegalStateException( "Must not be executed within a transaction context: " + callable );
        }
    }
}
