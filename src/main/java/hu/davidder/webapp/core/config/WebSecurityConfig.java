package hu.davidder.webapp.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import hu.davidder.webapp.core.filter.ApiKeyFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

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

	@Bean
	public ApiKeyFilter apiKeyFilter() {
		return new ApiKeyFilter(apiKey);
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
		.csrf(csrf -> csrf.disable())
		.cors(cors-> cors.configurationSource(corsConfigurationSource))
				.authorizeHttpRequests(
						auth -> 
						auth.requestMatchers(HttpMethod.GET,"/store/**").authenticated()
						.requestMatchers(HttpMethod.POST,"/store/**").authenticated()
						.requestMatchers(HttpMethod.DELETE,"/store/**").authenticated()
						.requestMatchers(HttpMethod.PATCH,"/store/**").authenticated()
						.requestMatchers("/pet/**").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class); 
		return http.build();
	}
}
