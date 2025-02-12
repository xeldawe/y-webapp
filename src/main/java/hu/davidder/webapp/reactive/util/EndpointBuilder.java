package hu.davidder.webapp.reactive.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.davidder.webapp.reactive.controller.ReactivePetController;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class EndpointBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ReactivePetController.class);
    private final ScheduledExecutorService logExecutor = Executors.newSingleThreadScheduledExecutor();
    private int maxRequestPerSecond = 1000;
    private final AtomicInteger requestCounter = new AtomicInteger(0);

    private final Vertx vertx;
    private final Router router;
    private Map<String, Route> routes = new HashMap<>();
    private String apiKey = null;

    public EndpointBuilder(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
        cors();
    }
    
    private void cors() {
    	router.route().handler(CorsHandler.create("*")
    	        .allowedMethod(HttpMethod.GET)
    	        .allowedMethod(HttpMethod.POST)
    	        .allowedMethod(HttpMethod.PUT)
    	        .allowedMethod(HttpMethod.DELETE)
    	        .allowedMethod(HttpMethod.OPTIONS)
    	        .allowedHeader("Access-Control-Allow-Origin")
    	        .allowedHeader("access-control-allow-methods")
    	        .allowedHeader("access-control-allow-headers")
    	        .allowedHeader("Authorization")
    	        .allowedHeader("Content-Type")
    	        .allowedHeader("origin")
    	        .allowedHeader("x-api-key")
    	        .allowCredentials(false));
    }

    public Route addEndpoint(String endpoint, HttpMethod httpMethod) {
        Route route = router.route(httpMethod, endpoint);

        routes.put(endpoint, route);
        if (!httpMethod.equals(HttpMethod.GET)) {
            route.handler(BodyHandler.create());
        }
        return route;
    }

    public void createServer(Promise<Void> startPromise, int port) {
        vertx.createHttpServer().requestHandler(router).listen(port).onComplete(http -> {
            if (http.succeeded()) {
                startPromise.complete();
                logAsync(() -> logger.info("HTTP server started on port " + port));
            } else {
                handleFailure(startPromise, http.cause());
            }
        });
    }

    public void enableLocalRateLimit(int maxRequestPerSecond, int resetPeriod) {
        this.maxRequestPerSecond = maxRequestPerSecond;
        vertx.setPeriodic(resetPeriod, id -> resetRequestCounter());
        for (Entry<String, Route> router : routes.entrySet()) {
            router.getValue().handler(this::rateLimitHandler);
        }
    }

    // https://www.keycloak.org/ should be better :)
    public void enableApiKey(String apiKey) {
        this.apiKey = apiKey;
        for (Entry<String, Route> router : routes.entrySet()) {
            router.getValue().handler(this::apiKeyFilter);
        }
    }

    private void handleFailure(Promise<Void> startPromise, Throwable cause) {
        logAsync(() -> logger.error("HTTP server failed to start", cause));
        startPromise.fail(cause);
    }

    private void logAsync(Runnable logTask) {
        logExecutor.schedule(logTask, 0, TimeUnit.MILLISECONDS);
    }

    public Route getRoute(String endpoint) {
        return routes.get(endpoint);
    }

    private int incrementRequestCounter() {
        return requestCounter.incrementAndGet();
    }

    private void apiKeyFilter(RoutingContext ctx) {
        String apiKey = ctx.request().getHeader("x-api-key");
        if (this.apiKey.equals(apiKey)) {
            ctx.next();
        } else {
            ctx.fail(401);
        }
    }

    private void rateLimitHandler(RoutingContext ctx) {
        int currentRequests = incrementRequestCounter();
        logAsync(() -> logger.info("Current request count: " + currentRequests));

        if (currentRequests > maxRequestPerSecond) {
            logAsync(() -> logger.warn("Too many requests: {}", currentRequests));
            ctx.response().setStatusCode(429).putHeader("content-type", "application/json")
                .end("{\"error\": \"Too many requests\"}");
        } else {
            ctx.next();
        }
    }

    private void resetRequestCounter() {
        logAsync(() -> logger.debug("Resetting request counter"));
        requestCounter.set(0);
    }
}
