package com.helpindia.Admin.config;

import com.helpindia.Admin.security.AdministratorUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AdministratorUserDetailsService adminUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(AdministratorUserDetailsService adminUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.adminUserDetailsService = adminUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * DaoAuthenticationProvider now needs a UserDetailsService in the constructor
     * (no-arg constructor/setter was removed in the version you're using).
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        // construct with UserDetailsService
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * SecurityFilterChain with updated httpBasic(...) usage.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        // register provider
        http.authenticationProvider(authProvider);

        http
                // disable CSRF for JSON API (enable if you use browser forms)
                .csrf(csrf -> csrf.disable())

                // authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/login", "/api/admin/register").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // httpBasic requires a Customizer in this version; use withDefaults()
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
