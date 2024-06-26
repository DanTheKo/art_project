package com.prototype.configuration;

import com.prototype.entities.User;
import com.prototype.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/posts/addOrUpdate/**").authenticated()
                        .requestMatchers("/posts/show/**").authenticated()
                        .requestMatchers("/posts/filter/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin((form) -> form
                        .loginPage("/posts/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/").permitAll()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            User user = userService.getUserByUserName(username);
            //userService.encode(username);
            return org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password(user.getPassword()).authorities(userService.getAuthorityByUsername(username)).build();
        };
    }
}