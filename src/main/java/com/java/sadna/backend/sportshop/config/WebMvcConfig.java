package com.java.sadna.backend.sportshop.config;

import com.java.sadna.backend.sportshop.security.JwtCookieAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

// Owns everything image- and CORS-related on the MVC side:
//   - configurePathMatch prepends app.api.path-prefix to every @RestController
//     so controllers + the OpenAPI-generated *Api interfaces stay prefix-free.
//   - CORS rules for ${app.api.path-prefix}/**.
//   - Static resource handler for /images/** (un-prefixed, served straight from disk).
//   - FilterRegistrationBean that wires ImageResponseHeadersFilter to /images/*
//     just before Spring Security in the filter chain.
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppProperties props;

    public WebMvcConfig(AppProperties props) {
        this.props = props;
    }

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                props.getApi().getPathPrefix(),
                HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(props.getApi().getPathPrefix() + "/**")
                .allowedOrigins(props.getCors().getAllowedOrigins().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders(
                        TraceIdResponseHeaderFilter.HEADER,
                        JwtCookieAuthenticationFilter.ROLE_HEADER)
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String urlPrefix = props.getImages().getUrlPrefix();
        Path absoluteDir = Path.of(props.getImages().getLocalDir()).toAbsolutePath().normalize();
        String location = absoluteDir.toUri().toString();

        registry.addResourceHandler("/" + urlPrefix + "/**")
                .addResourceLocations(location);
    }

    @Bean
    public FilterRegistrationBean<ImageResponseHeadersFilter> imageResponseHeadersFilterRegistration() {
        FilterRegistrationBean<ImageResponseHeadersFilter> registration =
                new FilterRegistrationBean<>(new ImageResponseHeadersFilter(props));
        registration.addUrlPatterns("/" + props.getImages().getUrlPrefix() + "/*");
        registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
        registration.setName("imageResponseHeadersFilter");
        return registration;
    }
}
