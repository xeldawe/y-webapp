package hu.davidder.webapp.reactive.auth;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.springframework.stereotype.Service;

import hu.davidder.webapp.reactive.util.CustomKeycloakConfig;

@Service
public class KeycloakService {

    private final WebClient webClient;
    private final CustomKeycloakConfig config;

    public KeycloakService(WebClient webClient, CustomKeycloakConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    public void revokeToken(String token) {
        System.out.println("config.getSite(): "+config.getSite());
        MultiMap form = MultiMap.caseInsensitiveMultiMap()
            .add("client_id", config.getClientId())
            .add("client_secret", config.getClientSecret())
            .add("grant_type", config.getGrantType())
            .add("username", config.getUsername())
            .add("password", config.getPassword())
            .add("token", token);

        webClient.postAbs(config.getSite()+config.getConnect()+"revoke")
            .putHeader("Content-Type", "application/x-www-form-urlencoded")
            .as(BodyCodec.string())
            .sendForm(form)
            .onSuccess(response -> {
                System.out.println("Token revoked successfully: " + response.body());
            })
            .onFailure(err->{
            	System.out.println(err.getMessage()); //TODO
            });
    }
    
    
}
