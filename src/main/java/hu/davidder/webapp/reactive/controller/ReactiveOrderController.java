package hu.davidder.webapp.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hu.davidder.webapp.core.base.order.service.OrderService;
import hu.davidder.webapp.reactive.util.EndpointBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;

@Component
public class ReactiveOrderController extends AbstractVerticle {

	@Value("${API_KEY:abc123}")
	private String apiKey;
	
    @Lazy
	@Autowired
	private OrderService orderService;
    
    @Autowired
    private EndpointBuilder endpointBuilder;
	
    private ObjectMapper objectMapper;
    
    public ReactiveOrderController() {
    	this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.registerModule(new Jdk8Module());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
    public void start(Promise<Void> startPromise) {
    	String base = "/store";
    	//Custom rate limit (better -> https://github.com/bucket4j/bucket4j - with redis combo)
    	Route getOrdersRoute = endpointBuilder.addEndpoint(base+"/orders", HttpMethod.GET);
    	Route getOrderRoute = endpointBuilder.addEndpoint(base+"/orders/:orderId", HttpMethod.GET);
    	endpointBuilder.enableOauth();
    	endpointBuilder.enableLocalRateLimit(1000,6000);
    	endpointBuilder.enableApiKey(apiKey);
    	//create pets route
    	getOrders(getOrdersRoute);
    	getOrder(getOrderRoute);
    	//start server
    	endpointBuilder.createServer(startPromise, 8988);
    }

    private void getOrders(Route route) {
        route.handler(req -> {
            try {
                String json = objectMapper.writeValueAsString(orderService.getAllOrders());
                req.response().putHeader("content-type", "application/json").end(json);
            } catch (JsonProcessingException e) {
                req.fail(500, e);
            }
        });
    }

    private void getOrder(Route route) {
        route.handler(req -> {
            String orderId = req.pathParam("orderId");
            try {
                String json = objectMapper.writeValueAsString(orderService.getOrderByIdCached(Long.valueOf(orderId)));
                req.response().putHeader("content-type", "application/json").end(json);
            } catch (JsonProcessingException | NumberFormatException e) {
            	 req.fail(500, e);
            }
        });
    }
    
    
}
    
