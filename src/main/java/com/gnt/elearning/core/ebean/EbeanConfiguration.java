package com.gnt.elearning.core.ebean;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.ServerConfig;
import io.ebean.spring.txn.SpringJdbcTransactionManager;
import java.sql.Driver;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Component
@Configuration
@EnableTransactionManagement
public class EbeanConfiguration {

  @Value("${datasource.db.username}")
  private String username;
  @Value("${datasource.db.password}")
  private String password;
  @Value("${datasource.db.databaseUrl}")
  private String databaseUrl;
  @Value("${datasource.db.databaseDriver}")
  private String databaseDriver;


  @Bean
  @Qualifier("ebean")
  public DataSource dataSource() throws ClassNotFoundException {
    SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
    simpleDriverDataSource.setUsername(username);
    simpleDriverDataSource.setPassword(password);
    simpleDriverDataSource.setUrl(databaseUrl);
    simpleDriverDataSource.setDriverClass((Class<? extends Driver>) Class.forName(databaseDriver));
    return simpleDriverDataSource;
  }

  @Bean
  public PlatformTransactionManager dataSourceTransactionManager() throws ClassNotFoundException {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public EbeanServer createEbeanServer(CurrentUserProvider currentUserProvider) throws ClassNotFoundException {
    ServerConfig config = new ServerConfig();
    config.setName("db");


    config.setDataSource(dataSource());
    config.setExternalTransactionManager(new SpringJdbcTransactionManager());
    config.setDefaultServer(true);
    config.setCurrentUserProvider(currentUserProvider);
    return EbeanServerFactory.create(config);
  }

  @Bean
  public CurrentUserProvider currentUserProvider() {
    return new CurrentUserProvider() {
      @Override
      public Object currentUser() {
        return "";
      }
    };
  }
}