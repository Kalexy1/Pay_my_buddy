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

import com.pay_my_buddy.model.User;
import com.pay_my_buddy.security.CustomUserDetailsService;
import com.pay_my_buddy.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
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


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
