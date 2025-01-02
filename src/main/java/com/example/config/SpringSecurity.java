package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Konfigurační třída pro nastavení Spring Security.
 * Zajišťuje autentizaci, autorizaci a správu přístupu k různým částem aplikace.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final UserDetailsService userDetailsService;

    // Konstruktor pro injektování UserDetailsService, který načítá uživatele z databáze
    public SpringSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Bean pro šifrování hesel pomocí BCrypt.
     * Používá se při registraci uživatelů a při autentizaci.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Hlavní konfigurace zabezpečení aplikace.
     * Řídí přístup na různé URL adresy a nastavuje pravidla pro přihlášení a odhlášení.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/register/**", "/index", "/", "/admin/**", "/reservations/**").permitAll()
                    //.requestMatchers("/admin/**").hasRole("ADMIN")  // Přístup na /admin pouze pro ADMIN
                    //.requestMatchers("/reservations/**").hasRole("USER")  // Přístup na /reservations pro USER
                    
                    .anyRequest().authenticated()  // Všechny ostatní žádosti vyžadují přihlášení
            )
            .formLogin(form -> form
                    .loginPage("/login")  // Vlastní stránka pro přihlášení
                    .loginProcessingUrl("/login")  // URL pro zpracování přihlášení
                    .successHandler(customSuccessHandler())  // Přesměrování po úspěšném přihlášení
                    .permitAll()  // Přístup k přihlašovací stránce má každý
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Odhlášení přes /logout
                    .logoutSuccessUrl("/index")  // Po úspěšném odhlášení přesměrování na index
                    .permitAll()  // Každý se může odhlásit
            );

        return http.build();
    }

    /**
     * Přizpůsobené přesměrování po úspěšném přihlášení.
     * Uživatel s rolí ADMIN bude přesměrován na /admin, ostatní na /reservations.
     */
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            authentication.getAuthorities().forEach(auth -> {
                try {
                    if (auth.getAuthority().equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin");  // Přesměrování pro admina
                    } else {
                        response.sendRedirect("/reservations");  // Přesměrování pro běžné uživatele
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }    

    /**
     * Konfigurace autentizace uživatelů.
     * Používá UserDetailsService pro načtení uživatelů a BCrypt pro porovnávání hesel.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
