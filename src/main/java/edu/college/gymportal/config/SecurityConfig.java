package edu.college.gymportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService users(PasswordEncoder encoder,
            @Value("${GYM_ADMIN_PASSWORD:}") String adminPassword,
            @Value("${GYM_STAFF_PASSWORD:}") String staffPassword) {
        if (adminPassword.isBlank() || staffPassword.isBlank()) {
            throw new IllegalStateException("Set GYM_ADMIN_PASSWORD and GYM_STAFF_PASSWORD before starting the portal.");
        }
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode(adminPassword)).roles("ADMIN").build(),
                User.withUsername("staff").password(encoder.encode(staffPassword)).roles("STAFF").build());
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/actuator/health").permitAll()
                        .requestMatchers("/api/members/*/status", "/members/*/status").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "STAFF")
                        .anyRequest().authenticated())
                .formLogin(form -> form.defaultSuccessUrl("/", true))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
                .build();
    }
}
