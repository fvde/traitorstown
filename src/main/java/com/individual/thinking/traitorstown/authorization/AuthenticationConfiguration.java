package com.individual.thinking.traitorstown.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AuthenticationConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationInterceptor")
    HandlerInterceptor securityControllerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityControllerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/users/login",
                        "/users/register",
                        "/trace",
                        "/metrics",
                        "/info",
                        "/health",
                        "/error");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
