package mk.ukim.finki.wp.molbi.config;

import mk.ukim.finki.wp.molbi.model.enums.AppRole;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

public class AuthConfig {

    public HttpSecurity authorize(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // Admin paths
                        .requestMatchers("/admin/**").hasAnyRole(
                                AppRole.ADMIN.name(),
                                AppRole.FINANCE_ADMIN.name()
                        )
                        // Professor paths (for late enrollment approval)
                        .requestMatchers("/professor/requests/**").hasAnyRole(
                                AppRole.PROFESSOR.name(),
                                AppRole.ADMIN.name()
                        )
                        // Student paths
                        .requestMatchers("/requests/**").hasAnyRole(
                                AppRole.STUDENT.name(),
                                AppRole.ADMIN.name()
                        )

                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
    }

}
