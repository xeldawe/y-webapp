package hu.davidder.webapp.reactive.auth;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import hu.davidder.webapp.reactive.util.CustomKeycloakConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;

@Service
@Scope("prototype")
public class AuthServer {
	private final Vertx vertx;
    private final CustomKeycloakConfig config;

	@Autowired
	public AuthServer(Vertx vertx, CustomKeycloakConfig config) {
		this.vertx = vertx;
        this.config = config;
	}
	

	private OAuth2Options getCredentials() {
		return new OAuth2Options().setClientId(config.getClientId()).setClientSecret(config.getClientSecret())
				.setSite(config.getSite()).setTenant("testrealm");
	}

	public Future<OAuth2Auth> keycloak() {
		return KeycloakAuth.discover(vertx, getCredentials());
	}

	public CompletableFuture<String> login(String username, String password) {
		Credentials tokenConfig = new UsernamePasswordCredentials(username, password);
		CompletableFuture<String> futureToken = new CompletableFuture<>();

		keycloak().onSuccess(oauth2 -> {
			oauth2.authenticate(tokenConfig).onSuccess(user -> {
				String token = user.principal().getString("access_token");
				System.out.println("Access Token: " + token);
				futureToken.complete(token);
			}).onFailure(err -> {
				System.err.println("Access Token Error: " + err.getMessage());
				futureToken.completeExceptionally(new RuntimeException("Access Token Error", err));
			});
		}).onFailure(err -> {
			System.err.println("Failed to discover Keycloak configuration: " + err.getMessage());
			futureToken.completeExceptionally(new RuntimeException("Failed to discover Keycloak configuration", err));
		});

		return futureToken;
	}

}
