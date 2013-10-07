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

import java.sql.SQLException;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.freiheit.sqlapi4j.domain.exception.DataAccessException;

// CSOFF:LineLength
/**
 * A MethodInterceptor for "Transaction"-Annotated methods.
 * Please note that the recommended way to inject dependencies into an Interceptor
 * is to have it injected _after_ construction.
 * So remember to call {@link AbstractModule#requestInjection} when you use the injector
 * in a module! Example is {@link TransactionModule}.
 *
 * @see <a href="http://code.google.com/p/google-guice/wiki/AOP#Injecting_Interceptors">http://code.google.com/p/google-guice/wiki/AOP#Injecting_Interceptors</a>
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 * @author Klas Kalass (klas.kalass@freiheit.com)
 */
// CSON:.
public class TransactionInterceptor implements MethodInterceptor {

    private static final class Delegate implements Callable<Object> {
        private final MethodInvocation _method;

        private Delegate(@Nonnull final MethodInvocation method) {
            _method = method;
        }

        @Override
        public Object call() throws SQLException {
            try {
                return _method.proceed();
            }// CHECKSTYLE:OFF the proceed-method throws Throwable.
            catch (final RuntimeException e) {
                // CHECKSTYLE:ON
                throw e;
            }// CHECKSTYLE:OFF the proceed-method throws Throwable.
            catch (final Error e) {
                // CHECKSTYLE:ON
                throw e;
            }
            // CHECKSTYLE:OFF the proceed-method throws Throwable.
            catch (final Throwable e) {
                // CHECKSTYLE:ON
                throw new DataAccessException(e);
            }
        }
    }

    private EnhancedTransactionTemplate _transactionTemplate;



    @Override
    // CHECKSTYLE:OFF Must throw throwable: the interface says so
    public Object invoke(final MethodInvocation method) throws Throwable {
        // CHECKSTYLE:ON

        final Transactional transactional = method.getMethod().getAnnotation(Transactional.class);
        if (transactional == null) {
            // This interceptor is meant to be used in combination with the Transactional annotation
            throw new DataAccessException("Expected a Transactional annotation.");
        }

        try {
            final Delegate callback = new Delegate(method);
            if (transactional.propagateNew()) {
                return _transactionTemplate.executePropagateNew(callback);
            } else {
                return _transactionTemplate.execute(callback);
            }
        }
        catch (final DataAccessException e) {
            throw e.getCause();
        }
    }

}
