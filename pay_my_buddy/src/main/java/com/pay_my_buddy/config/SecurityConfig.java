package com.pay_my_buddy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.pay_my_buddy.security.CustomUserDetailsService;

/**
 * Classe de configuration de la sécurité de l'application.
 * <p>
 * Cette classe définit les règles de sécurité pour l'authentification et l'autorisation des utilisateurs,
 * en utilisant Spring Security.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructeur de la classe SecurityConfig.
     *
     * @param userDetailsService Service personnalisé de gestion des utilisateurs.
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Définit la configuration de la chaîne de filtres de sécurité.
     * <p>
     * Cette méthode désactive CSRF, autorise certaines routes publiques et
     * configure le formulaire de connexion et de déconnexion.
     * </p>
     *
     * @param http Objet {@link HttpSecurity} pour configurer les règles de sécurité.
     * @return {@link SecurityFilterChain} contenant la configuration de sécurité.
     * @throws Exception En cas d'erreur lors de la configuration de la sécurité.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Initialisation de la configuration de sécurité...");

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/transfer", true)
                .failureUrl("/login?error")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        logger.info("Sécurité configurée avec succès !");
        return http.build();
    }

    /**
     * Définit un encodeur de mot de passe sécurisé.
     * <p>
     * Utilise BCrypt pour hacher les mots de passe avant stockage.
     * </p>
     *
     * @return {@link PasswordEncoder} implémentant BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
