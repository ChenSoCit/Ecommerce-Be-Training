package com.java.TrainningJV.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// ap dung cho tat ca cac enpoint
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true) // cho phep cooke/token
                .maxAge(3600); // cache preflight request trong 1h
    }
}
