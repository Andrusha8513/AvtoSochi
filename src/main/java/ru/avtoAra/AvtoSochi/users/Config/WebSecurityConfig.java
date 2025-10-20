package ru.avtoAra.AvtoSochi.users.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private DataSource dataSource;



    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public JdbcUserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "select email, password, enabled from users where email=?");
        manager.setAuthoritiesByUsernameQuery(
                "select u.email, ur.roles from users u inner join users_role ur on u.id = ur.users_id where u.email=?");

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                        .maximumSessions(5)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/createProduct").hasRole("ADMIN")
                        .requestMatchers("/", "/home.html",
                                "/login.html",
                                "/registration.html",
                                "forgot-password.html",
                                "/css/**", "/js/**", "/images/**",
                                "/api/users/registration",
                                "/api/users/confirm/**",
                                "/api/users/password/**",
                                "/api/users/resend-code" ).permitAll()
                        .requestMatchers("/", "registration", "/api/users/registration", "/api/users/confirm/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/hello", true)
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .alwaysRemember(true)
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(86400 * 365)
                        .userDetailsService(userDetailsManager())
                )
                .logout((logout) -> logout
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll());

        return http.build();
    }

}
//    @Scheduled(fixedRate = 60000) // Очистка каждые 60 секунд
//    public void cleanupExpiredTokens(){
//        try {
//            try (var connection = dataSource.getConnection();
//                 var statement = connection.createStatement()) {
//
//                int deletedCount = statement.executeUpdate(
//                        "DELETE FROM persistent_logins WHERE last_used < NOW() - INTERVAL '10 seconds'"
//                );
//
//                if (deletedCount > 0) {
//                    System.out.println("✓ Очищено " + deletedCount + " просроченных токенов");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("✗ Ошибка очистки токенов: " + e.getMessage());
//        }
//    }