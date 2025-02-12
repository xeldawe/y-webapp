package hu.davidder.webapp.reactive.util;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

@Component
public class SpringVerticleFactory implements VerticleFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "spring";
    }

    @Override
    public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
        try {
            String clazz = VerticleFactory.removePrefix(verticleName);
            System.out.println("Creating verticle for class: " + clazz); // Debug log
            Verticle verticle = (Verticle) applicationContext.getBean(Class.forName(clazz));
            promise.complete(() -> verticle);
        } catch (Exception e) {
            System.err.println("Failed to create verticle: " + e.getMessage()); // Debug log
            promise.fail(e);
        }
    }

}

