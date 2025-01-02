package com.example.service;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Třída implementující UserDetailsService pro autentizaci uživatelů pomocí Spring Security.
 * Načítá uživatele z databáze na základě e-mailu a mapuje role na oprávnění (GrantedAuthority).
 */
@Service
public class AuthentizationService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Konstruktor pro injektování UserRepository.
     * @param userRepository Repozitář pro práci s uživateli.
     */
    public AuthentizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Načte uživatele na základě e-mailu pro autentizaci.
     * @param email E-mail uživatele.
     * @return UserDetails objekt obsahující informace o uživateli.
     * @throws UsernameNotFoundException Výjimka, pokud uživatel s daným e-mailem neexistuje.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Hledání uživatele podle e-mailu
        User user = userRepository.findByEmail(email);
    
        if (user != null) {
            // Vytvoření UserDetails objektu na základě dat uživatele
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    mapRolesToAuthorities(user.getRoles()));  // Přiřazení rolí jako GrantedAuthority
        } else {
            // Výjimka, pokud uživatel nebyl nalezen
            throw new UsernameNotFoundException("Nesprávné jméno nebo heslo.");
        }
    }
    
    /**
     * Převede role uživatele na kolekci GrantedAuthority (oprávnění).
     * @param roles Kolekce rolí uživatele.
     * @return Kolekce oprávnění (GrantedAuthority).
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        // Mapování rolí na autority (ROLE_ADMIN -> SimpleGrantedAuthority("ROLE_ADMIN"))
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
