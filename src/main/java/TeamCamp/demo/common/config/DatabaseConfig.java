package TeamCamp.demo.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


import static TeamCamp.demo.common.config.DatabaseConfig.RDS_DOMAIN;

@Configuration
@EnableJpaRepositories( basePackages =  {RDS_DOMAIN})
public class DatabaseConfig {

    static final String RDS_DOMAIN = "TeamCamp";
    @Autowired
    private DatabaseProperty databaseProperty;

    public DataSource routingDataProperty(String url){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(databaseProperty.getUrl());
        hikariDataSource.setDriverClassName(databaseProperty.getDriverClassName());
        hikariDataSource.setPassword(databaseProperty.getPassword());
        hikariDataSource.setUsername(databaseProperty.getUsername());

        return hikariDataSource;
    }


    @Bean
    public DataSource routingDataSource(){
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
        DataSource master = routingDataProperty(databaseProperty.getUrl());

        Map<Object,Object> dataSourceMap = new LinkedHashMap<>();
        dataSourceMap.put("master",master);

        databaseProperty.getSlaveList().forEach(slave -> {
            dataSourceMap.put(slave.getName() , routingDataProperty(slave.getUrl()));
        });

        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
        replicationRoutingDataSource.setDefaultTargetDataSource(master);
        return replicationRoutingDataSource;
    }

    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("TeamCamp");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);


        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }
}
