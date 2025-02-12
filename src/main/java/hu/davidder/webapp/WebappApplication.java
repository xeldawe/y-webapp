package hu.davidder.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import hu.davidder.webapp.reactive.controller.ReactiveOrderController;
import hu.davidder.webapp.reactive.controller.ReactivePetController;
import hu.davidder.webapp.reactive.util.SpringVerticleFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class WebappApplication implements CommandLineRunner {

	@Autowired
	private Vertx vertx;

	@Autowired
	private SpringVerticleFactory verticleFactory;

	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Deploy vertx");
		vertx.registerVerticleFactory(verticleFactory);
        vertx.deployVerticle(verticleFactory.prefix() + ":" + ReactivePetController.class.getName(),
                new DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD), res -> {
                    if (res.succeeded()) {
                        System.out.println("ReactivePetController deployed successfully.");
                    } else {
                        System.err.println("Failed to deploy ReactivePetController: " + res.cause());
                    }
                });

        vertx.deployVerticle(verticleFactory.prefix() + ":" + ReactiveOrderController.class.getName(),
                new DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD), res -> {
                    if (res.succeeded()) {
                        System.out.println("ReactiveOrderController deployed successfully.");
                    } else {
                        System.err.println("Failed to deploy ReactiveOrderController: " + res.cause());
                    }
                });
	}
}
