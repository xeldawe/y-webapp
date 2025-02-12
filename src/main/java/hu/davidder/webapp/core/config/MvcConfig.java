package hu.davidder.webapp.core.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hu.davidder.webapp.core.interceptor.LoggerInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    @Lazy
    private LoggerInterceptor loggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggerInterceptor).addPathPatterns(Arrays.asList("/**"));
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//            registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                .allowedHeaders("Authorization", "Origin", "Content-Type", "Accept", "x-api-key", "Access-Control-Allow-Methods", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers")
//                .exposedHeaders("Authorization", "Content-Disposition")
//                .allowCredentials(true);
//    }
}