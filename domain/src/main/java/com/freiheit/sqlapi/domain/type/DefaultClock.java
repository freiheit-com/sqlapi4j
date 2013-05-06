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
package com.freiheit.sqlapi.domain.type;

import java.util.Calendar;


/**
 * A {@link Clock} which always returns the current time.
 *
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 */
public final class DefaultClock implements Clock<Calendar> {

    public static final Clock<Calendar> INSTANCE = new DefaultClock();

    private DefaultClock() {
        super();
    }

    @Override
    public Calendar getCurrentTime() {
        return Calendar.getInstance();
    }

}
