package hu.davidder.webapp.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import hu.davidder.webapp.core.filter.ApiKeyFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private ApiKeyFilter apiKeyFilter;

    @Bean
    @Order(1)
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(apiKeyFilter);
        registrationBean.addUrlPatterns("/store/*");
        return registrationBean;
    }
}
