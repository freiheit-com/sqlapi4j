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
package com.freiheit.sqlapi.dao.meta;

import javax.annotation.ParametersAreNonnullByDefault;

import com.freiheit.sqlapi.domain.type.Clock;


/**
 * Marker interface for a timestamp column which is automatically set to the current time upon modification of the record.
 *
 * @param <C> the clock type
 *
 * @see Clock
 *
 * @author Carsten Luckmann (carsten.luckmann@freiheit.com)
 */
@ParametersAreNonnullByDefault
public interface UpdatedAtColumn<C> extends ClockColumn<C> {

}
