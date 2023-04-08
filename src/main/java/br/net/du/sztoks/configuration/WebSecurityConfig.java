package br.net.du.sztoks.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        // set the name of the attribute the CsrfToken will be populated on
        final CsrfTokenRequestAttributeHandler requestHandler =
                new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http.authorizeHttpRequests(
                        (authz) ->
                                authz.requestMatchers("/resources/**", "/signup")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .formLogin((form) -> form.loginPage("/login").permitAll())
                .logout((logout) -> logout.permitAll())

                // Setting to false to enable auto-login upon sign-up
                .securityContext((securityContext) -> securityContext.requireExplicitSave(false))

                // New Spring Security 6 defaults
                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(final HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}
