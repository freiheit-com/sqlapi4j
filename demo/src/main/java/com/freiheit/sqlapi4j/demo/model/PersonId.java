package com.freiheit.sqlapi4j.demo.model;

import javax.annotation.Nonnull;

import com.freiheit.sqlapi4j.domain.type.Id;

public class PersonId extends Id {
	private static final long serialVersionUID = 1L;

	private PersonId(final long value) {
        super(value);
    }

    @Nonnull
    public static PersonId valueOf(final long value) {
        return new PersonId(value);
    }

}