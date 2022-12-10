package web.config;

import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({"classpath:db.properties"})
@EnableTransactionManagement
@ComponentScan({"web"})
public class DBConfig {
    @Autowired
    private Environment env;

    public DBConfig() {
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName((String)Objects.requireNonNull(this.env.getProperty("db.driver")));
        dataSource.setUrl(this.env.getProperty("db.url"));
        dataSource.setUsername(this.env.getProperty("db.username"));
        dataSource.setPassword(this.env.getProperty("db.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(this.getDataSource());
        entityManagerFactoryBean.setPackagesToScan(new String[]{"web"});
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(this.getHibernateProperties());
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(this.getEntityManagerFactoryBean().getObject());
        return transactionManager;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", this.env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", this.env.getProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }
}
