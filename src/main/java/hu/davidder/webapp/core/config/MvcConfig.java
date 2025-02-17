package hu.davidder.webapp.core.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
    
}