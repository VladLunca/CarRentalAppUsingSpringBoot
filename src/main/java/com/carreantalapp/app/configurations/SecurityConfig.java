package com.carreantalapp.app.configurations;

import com.carreantalapp.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/registration/**","/css/**","/js/**","/images/**","/uploads/**").permitAll()
                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login")
                .defaultSuccessUrl("/home",true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
              ROLE_SUPER_ADMIN > ROLE_MANAGER
              ROLE_MANAGER > ROLE_EMPLOYEE
              ROLE_EMPLOYEE > ROLE_CUSTOMER
              """);
    }

}
