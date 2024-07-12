package com.example.minorproject1.config;

import com.example.minorproject1.model.Authority;
import com.example.minorproject1.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            UserService userService,
            PasswordEncoder passwordEncoder
    ){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                (authz) -> authz
                        .requestMatchers("/students/admin/**").hasAuthority(Authority.ADMIN.name())
                        .requestMatchers("/students/**").hasAuthority(Authority.STUDENT.name())
                        .requestMatchers("/admin/**").hasAuthority(Authority.ADMIN.name())
//                        .requestMatchers(HttpMethod.GET,"/books/ineligible/**").hasAuthority(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,"/books/**").hasAnyAuthority(Authority.ADMIN.name(), Authority.STUDENT.name())
                        .requestMatchers("/books/**").hasAuthority(Authority.ADMIN.name())
                        .requestMatchers("/transactions/**").hasAuthority(Authority.STUDENT.name())
                        .requestMatchers("/**").permitAll()
        )

                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
