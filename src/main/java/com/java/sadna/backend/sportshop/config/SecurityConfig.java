package com.java.sadna.backend.sportshop.config;

import com.java.sadna.backend.sportshop.repository.UserRepository;
import com.java.sadna.backend.sportshop.security.JwtCookieAuthenticationFilter;
import com.java.sadna.backend.sportshop.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Owns the cross-cutting bits (stateless sessions, JWT cookie filter, formLogin
// off, CORS). Authorization is delegated to controller methods via @EnableMethodSecurity:
// anyRequest().permitAll() leaves every endpoint open by default, and methods opt in to
// gating with @PreAuthorize("isAuthenticated()") or @PreAuthorize("hasRole('ADMIN')").
// Convention: no annotation = public; add @PreAuthorize whenever an endpoint should NOT be.
//
// CSRF is disabled. The auth cookies are HttpOnly + SameSite=Lax (set in CookieService),
// the API is JSON-only, and CORS allowedOrigins is a tight whitelist -- which together
// already block the cross-site form-POST and cross-origin XHR-with-credentials attack
// vectors that CSRF tokens would otherwise defend against.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserRepository userRepository) throws Exception {
        JwtCookieAuthenticationFilter jwtCookieFilter = new JwtCookieAuthenticationFilter(jwtService, userRepository);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
