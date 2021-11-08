package TeamCamp.demo.common.config;

import TeamCamp.demo.common.database.DbProperties;
import TeamCamp.demo.common.database.ReplicationRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class DbConfig {
    private final DbProperties dbProperties;

    //routingDataSource 에서 사용할 메서드
    public DataSource createDataSource(String url){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setDriverClassName(dbProperties.getDriverClassName());
        hikariDataSource.setUsername(dbProperties.getUsername());
        hikariDataSource.setPassword(dbProperties.getPassword());

        return hikariDataSource;
    }


    @Bean
    public DataSource routingDataSource(){
        //AbstractRoutingDataSource를 상송받아 재정의한 ReplicationRoutingDataSource 생성
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();

        //master와 slave 정보를 key(name) , value(datasource) 형식으로 맵에 저장
        Map<Object,Object>dataSourceMap = new LinkedHashMap<>();
        DataSource masterDataSource  = createDataSource(dbProperties.getUrl());
        dataSourceMap.put("master",masterDataSource);


        dbProperties.getSlaveList().forEach(slave -> {
            dataSourceMap.put(slave.getName(),createDataSource(slave.getUrl()));
        });

        //ReplicationROutingDataSource의 replicationRoutingDataSourceNameList 세팅 -> slave 키 이름 리스트 세팅
        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);

        //default 값은 master
        replicationRoutingDataSource.setDefaultTargetDataSource(masterDataSource);

        return replicationRoutingDataSource;
    }
    @Bean
    public DataSource dataSource(){
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }

    //JPA EntityManager 설정
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("TeamCamp.demo");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        return entityManagerFactoryBean;
    }

    //JPA에서 사용할 TransactionManager 설정
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    //JDBC Template 빈 등록
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
