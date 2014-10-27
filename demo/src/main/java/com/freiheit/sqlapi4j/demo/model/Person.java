package com.freiheit.sqlapi4j.demo.model;

import javax.annotation.Nonnull;

public class Person {

    @Nonnull private final PersonId id;
    @Nonnull private final String firstName;
    @Nonnull private final String lastName;
    private final int height;
    @Nonnull private final Gender gender;

    public Person(
		@Nonnull final PersonId id, 
		@Nonnull final String firstName, 
		@Nonnull final String lastName, 
		final int height, 
		@Nonnull final Gender gender
	) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.gender = gender;
    }

    @Nonnull
    public PersonId getId() {
        return id;
    }

    @Nonnull
    public String getFirstName() {
        return firstName;
    }

    @Nonnull
    public String getLastName() {
        return lastName;
    }

    public int getHeight() {
        return height;
    }

    @Nonnull
    public Gender getGender() {
        return gender;
    }

}