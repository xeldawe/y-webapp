package hu.davidder.webapp.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.webapp.core.base.pet.service.PetService;
import hu.davidder.webapp.reactive.util.EndpointBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;

@Component
public class ReactivePetController extends AbstractVerticle {

    @Lazy
    @Autowired
    private PetService petService;;

    @Override
    public void start(Promise<Void> startPromise) {
    	EndpointBuilder endpointBuilder = new EndpointBuilder(vertx);
    	Route pets = endpointBuilder.addEndpoint("/pets", HttpMethod.GET);
    	//Custom rate limit (better -> https://github.com/bucket4j/bucket4j - with redis combo)
    	endpointBuilder.enableLocalRateLimit(1000,6000);
    	//create pets route
    	pets(pets);
    	//start server
    	endpointBuilder.createServer(startPromise, 8989);
    }

    private void pets(Route route) {
        route.handler(req -> {
            try {
                req.response().putHeader("content-type", "application/json").end(new ObjectMapper().writeValueAsString(petService.getAllPets()));
            } catch (JsonProcessingException e) {
            	//TODO
            }
        });
    }
}
    