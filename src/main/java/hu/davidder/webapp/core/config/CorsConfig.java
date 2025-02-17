package hu.davidder.webapp.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Value("${API_URL:http://34.79.119.8}")
	private String frontendUrl;

	@Bean
	@Primary
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("http://localhost:4201");
		corsConfiguration.addAllowedOrigin("http://localhost:4200");
		corsConfiguration.addAllowedOrigin("http://localhost:4000");
		corsConfiguration.addAllowedOrigin(frontendUrl + ":4200");
		corsConfiguration.addAllowedMethod("GET");
		corsConfiguration.addAllowedMethod("POST");
		corsConfiguration.addAllowedMethod("PUT");
		corsConfiguration.addAllowedMethod("DELETE");
		corsConfiguration.addAllowedMethod("OPTIONS");
		corsConfiguration.addAllowedMethod("PATCH");
		corsConfiguration.addAllowedHeader("Authorization");
		corsConfiguration.addAllowedHeader("Origin");
		corsConfiguration.addAllowedHeader("Content-Type");
		corsConfiguration.addAllowedHeader("Accept");
		corsConfiguration.addAllowedHeader("x-api-key");
		corsConfiguration.addAllowedHeader("Access-Control-Allow-Methods");
		corsConfiguration.addAllowedHeader("Access-Control-Allow-Origin");
		corsConfiguration.addAllowedHeader("Access-Control-Allow-Headers");
		corsConfiguration.addExposedHeader("Authorization");
		corsConfiguration.addExposedHeader("Content-Disposition");
		corsConfiguration.addExposedHeader("Access-Control-Allow-Origin");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
}