package hu.davidder.webapp.core.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

@Configuration
public class VertxConfig {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx(new VertxOptions().setWorkerPoolSize(24));
    }

    @Bean
    public ApplicationListener<ContextClosedEvent> vertxShutdownListener(Vertx vertx) {
        return event -> {
            System.out.println("Shutting down Vert.x instance...");
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    System.out.println("Vert.x instance closed.");
                } else {
                    System.err.println("Error during Vert.x shutdown: " + ar.cause());
                }
            });
        };
    }
}

