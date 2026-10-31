package com.example.ticketing.config;

import com.example.ticketing.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                  JwtAuthenticationFilter jwtAuthenticationFilter,
                                                  UserDetailsService userDetailsService,
                                                  PasswordEncoder passwordEncoder) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/venues", "/api/venues/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events/*/seats", "/api/events/*/available-seats").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/analytics/**").hasAnyRole("ADMIN", "ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/events/**", "/api/venues/**").hasAnyRole("ADMIN", "ORGANIZER")
                .requestMatchers(HttpMethod.PUT, "/api/events/**", "/api/venues/**").hasAnyRole("ADMIN", "ORGANIZER")
                .requestMatchers(HttpMethod.DELETE, "/api/events/**", "/api/venues/**").hasAnyRole("ADMIN", "ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/bookings/**", "/api/tickets/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
