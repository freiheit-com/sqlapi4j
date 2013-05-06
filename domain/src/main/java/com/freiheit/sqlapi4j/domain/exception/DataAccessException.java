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
package com.freiheit.sqlapi4j.domain.exception;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Is thrown if the database cannot be accessed (network down, sql-error etc).
 *
 * @author Tom Vollerthun (tom.vollerthun@freiheit.com)
 */
@ParametersAreNonnullByDefault
public class DataAccessException extends RuntimeException {

    public DataAccessException(final Throwable cause) {
        super(cause);
    }

    public DataAccessException(final String message) {
        super(message);
    }

    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
