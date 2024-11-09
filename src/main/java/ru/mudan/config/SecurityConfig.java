package ru.mudan.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("MultipleStringLiterals")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(matcherRegistry ->

                        matcherRegistry
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/registration/**",
                                        "/subjects/**",
                                        "/classes/**").hasAnyRole("ADMIN")
                                .requestMatchers("/schedules/**",
                                        "/grades/**",
                                        "/homeworks/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                                .requestMatchers("/student/**").hasAnyRole("STUDENT")
                                .requestMatchers("/teacher/**").hasAnyRole("TEACHER")
                                .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .authenticationProvider(authProvider);
        return http.build();
    }
}

