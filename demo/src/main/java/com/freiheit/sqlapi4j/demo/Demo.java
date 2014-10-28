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
package com.freiheit.sqlapi4j.demo;

import java.util.List;

import com.freiheit.sqlapi4j.demo.dao.PersonDao;
import com.freiheit.sqlapi4j.demo.guice.DbModule;
import com.freiheit.sqlapi4j.demo.guice.DemoModule;
import com.freiheit.sqlapi4j.demo.model.Gender;
import com.freiheit.sqlapi4j.demo.model.Person;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Demo {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new DemoModule(), new DbModule());
		
		// FIXME: Create the Person table!
		
		
		PersonDao dao = injector.getInstance(PersonDao.class);
		
		// setup data
		dao.insertPerson("Test", "User", 180, Gender.MALE);
		dao.insertPerson("Testerin", "User", 183, Gender.FEMALE);
		
		// query data
		List<Person> persons = dao.findByName("Test", "User");
		System.out.println("Found " + persons.size() + " persons.");
		for (Person person: persons) {
			System.out.println(person.getId() + ": " + person.getFirstName() + " " + person.getLastName() + ", " + person.getGender() + ", " + person.getHeight() + "cm");
		}
	}
}
