package com.freiheit.sqlapi4j.demo.guice;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.freiheit.sqlapi4j.dao.EnhancingExecutor;
import com.freiheit.sqlapi4j.dao.StatementEnhancer;
import com.freiheit.sqlapi4j.dao.meta.IdConverter;
import com.freiheit.sqlapi4j.dao.meta.IdDbType;
import com.freiheit.sqlapi4j.domain.type.Id;
import com.freiheit.sqlapi4j.generate.impl.ConverterRegistry;
import com.freiheit.sqlapi4j.generate.impl.H2DbDialect;
import com.freiheit.sqlapi4j.query.SqlExecutor;
import com.freiheit.sqlapi4j.query.impl.SqlExecutorImpl;
import com.freiheit.sqlapi4j.txguice.TransactionModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DbModule extends AbstractModule {

	@Override
	protected void configure() {
		// needs DataSource
		install(new TransactionModule());
	}
	
	@Provides
	public DataSource provideDataSource() {
		// Of course you could use any other Java DataSource implementation here
		final ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		
		return dataSource;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Provides
	public ConverterRegistry provideConverterRegistry() {
		ConverterRegistry converters = new ConverterRegistry(null /*no parent*/);
		// Register custom Converters, for example IdConverter
		converters.registerConverter((Class) IdDbType.class, new IdConverter<Id>());
		return converters;
	}	        
	
	@Provides
	public SqlExecutor provideSqlExecutor(ConverterRegistry converters) {
		SqlExecutor executor = new SqlExecutorImpl(new H2DbDialect(converters));
		
		List<StatementEnhancer> enhancers = new ArrayList<StatementEnhancer>();/*Use this feature for transparent operations, like setting updated_at and created_at*/
		return new EnhancingExecutor(executor, enhancers);
	}
}