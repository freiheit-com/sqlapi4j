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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * <p>Base class for all primary key values of type long.</p>
 *
 * <p>All child classes <b>must</b> provide a static factory method <code>valueOf(long)</code>.</p>
 */
@ParametersAreNonnullByDefault
public abstract class Id implements Serializable {

    private final long _value;

    protected Id(final long value) {
        this._value = value;
    }

    /**
     * General method for creating Id values.
     *
     * @throws IllegalArgumentException if we cannot create the requested id
     */
    @Nonnull
    public static <T extends Id> T valueOf(final Class<T> cls, final long value) throws IllegalArgumentException {
        try {
            final Method method = cls.getMethod("valueOf", long.class);
            final Object object = method.invoke(null, Long.valueOf(value));
            return cls.cast(object);
        } catch (final SecurityException e) {
            throw new IllegalArgumentException("Not a valid Id class: " + cls, e);
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException("Not a valid Id class: " + cls, e);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Not a valid Id class: " + cls, e);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException("Not a valid Id class: " + cls, e);
        } catch (final InvocationTargetException e) {
            throw new IllegalArgumentException("Not a valid Id class: " + cls, e);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        // only ids of the same id class can be equal
        if (obj instanceof Id && this.getClass().equals(obj.getClass())) {
            final Id other = (Id) obj;
            return other._value == this._value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // same implementation as java.lang.Long#hashCode()
        return (int) (_value ^ (_value >>> 32));
    }

    public long getValue() {
        return _value;
    }

    /**
     * @return the Long-Value of this id
     */
    @Nonnull
    public Long asLong() {
        return Long.valueOf(_value);
    }

    /**
     * @return the long-value in a String.
     */
    @Nonnull
    public String asString() {
        return Long.toString(_value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + _value + "]";
    }

}
