package com.freiheit.sqlapi4j.demo.guice;

import com.freiheit.sqlapi4j.demo.dao.PersonDao;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class DemoModule extends AbstractModule {
	@Override
	protected void configure() {
		// Needs:
		// TransactionTemplate (provided by TransactionModule), SqlExecutor (provided by DbModule)
		bind(PersonDao.class).in(Scopes.SINGLETON);
	}
}