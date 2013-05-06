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
package com.freiheit.sqlapi4j.domain.type;

import java.util.Calendar;

import javax.annotation.Nonnull;

/**
 * Returns the current time.
 *
 * To be more exact: This is an abstraction of time in order to gain control
 * over what is to be taken as the current time.
 *
 * @param <T> the type of the time object, e. g. {@link java.util.Date}, {@link Calendar}, or joda's DateTime
 *
 * @author Klas Kalass (klas.kalass@freiheit.com)
 */
public interface Clock<T> {

    /**
     * Returns the current time.
     */
    @Nonnull
    T getCurrentTime();

}
