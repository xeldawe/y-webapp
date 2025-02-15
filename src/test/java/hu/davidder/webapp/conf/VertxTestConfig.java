package hu.davidder.webapp.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import hu.davidder.webapp.reactive.util.SpringVerticleFactory;
import io.vertx.core.Vertx;

@TestConfiguration
public class VertxTestConfig {

    @Bean
    @Primary
    public Vertx vertx() {
        return Vertx.vertx();
    }
    
    @Bean
    @Primary
    public SpringVerticleFactory springVerticleFactory() {
        return new SpringVerticleFactory();
    }
}
