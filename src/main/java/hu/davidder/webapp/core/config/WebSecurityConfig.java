package hu.davidder.webapp.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfigurationSource;

import hu.davidder.webapp.core.filter.ApiKeyFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
	}

	// old one without spring security
//    @Autowired
//    private ApiKeyFilter apiKeyFilter;
//
//
//    @Bean
//    @Order(1)
//    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration() {
//        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(apiKeyFilter);
//        registrationBean.addUrlPatterns("/store/*");
//        return registrationBean;
//    }

	@Value("${API_KEY:abc123}")
	private String apiKey;

	@Value("${KEYCLOAK_ENABLED:false}")
	private boolean keycloakEnabled;

	@Value("${security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;

	@Bean
	public ApiKeyFilter apiKeyFilter() {
		return new ApiKeyFilter(apiKey);
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter() {
			protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
				Map<String, Object> realmAccess = jwt.getClaim("realm_access");
				Collection<GrantedAuthority> authorities = Optional.ofNullable(realmAccess)
						.map(access -> (Collection<String>) access.get("roles")).orElse(Collections.emptyList())
						.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

				authorities.addAll(grantedAuthoritiesConverter.convert(jwt));
				return authorities;
			}
		};

		return jwtAuthenticationConverter;
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, JwtAuthenticationConverter jwtAuthenticationConverter)
			throws Exception {
		if (keycloakEnabled) {
			configureKeycloakSecurity(http, corsConfigurationSource,jwtAuthenticationConverter);
		} else {
			configureSpringSecurity(http, corsConfigurationSource);
		}
		return http.build();
	}

	private void configureKeycloakSecurity(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, JwtAuthenticationConverter jwtAuthenticationConverter)
			throws Exception {
		getHttpBase(http, corsConfigurationSource)
				//.addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class)
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

	}

	private void configureSpringSecurity(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		getHttpBase(http, corsConfigurationSource).addFilterBefore(apiKeyFilter(),
				UsernamePasswordAuthenticationFilter.class);
	}

	private HttpSecurity getHttpBase(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		return http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource))
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/store/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/store/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/store/**").authenticated()
						.requestMatchers(HttpMethod.PATCH, "/store/**").authenticated().requestMatchers("/pet/**")
						.permitAll().anyRequest().permitAll())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}

	@Bean
	public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	@Bean
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new NullAuthenticatedSessionStrategy();
	}

}
