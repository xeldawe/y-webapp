package hu.davidder.webapp.core.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Value("${SPRING_DATASOURCE_URL:jdbc:postgresql://127.0.0.1:7000/petstore}")
	private String url;

	@Value("${SPRING_DATASOURCE_USERNAME:pet}")
	private String username;

	@Value("${SPRING_DATASOURCE_PASSWORD:meow}")
	private String password;

	@Primary
    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .url(url)
            .username(username)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .build();
    }

}