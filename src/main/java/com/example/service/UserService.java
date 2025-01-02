package com.example.service;

import com.example.dto.UserDto;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servisní třída pro správu uživatelů.
 * Poskytuje CRUD operace nad entitou User a zajišťuje registraci a správu rolí.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Konstruktor pro injektování repozitářů a encoderu hesel.
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Uloží nového uživatele na základě DTO objektu.
     * Automaticky přiřadí roli "ROLE_USER".
     */
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));  // Šifrování hesla

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = createDefaultRole();
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    /**
     * Vytvoří roli "ROLE_USER", pokud neexistuje.
     */
    private Role createDefaultRole() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    /**
     * Vyhledá uživatele podle e-mailu.
     * @param email E-mail uživatele.
     * @return Uživatel nebo null, pokud nebyl nalezen.
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    /**
     * Vyhledá uživatele podle e-mailu.
     * @param email E-mail uživatele.
     * @return Uživatel nebo null, pokud nebyl nalezen.
     */
    public User findUserByEmailWithRoles(String email) {
        return userRepository.findByEmailWithRoles(email);
    }

    /**
     * Vrátí seznam všech uživatelů ve formě DTO objektů.
     * @return Seznam uživatelů.
     */
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Najde uživatele podle ID.
     * @param id ID uživatele.
     * @return Uživatel nebo výjimka, pokud nebyl nalezen.
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Uživatel s ID " + id + " nebyl nalezen."));
    }

    /**
     * Smaže uživatele podle ID.
     * @param id ID uživatele.
     */
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Aktualizuje informace o uživateli.
     * @param id ID uživatele k aktualizaci.
     * @param updatedUser Aktualizované údaje o uživateli.
     */
    public void updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Uživatel s ID " + id + " nebyl nalezen."));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        userRepository.save(existingUser);
    }

    /**
     * Převod entity User na DTO objekt UserDto.
     * @param user Entita uživatele.
     * @return DTO reprezentace uživatele.
     */
    private UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        String[] nameParts = user.getName().split(" ", 2);  // Rozdělení jména na křestní jméno a příjmení
        userDto.setFirstName(nameParts[0]);
        userDto.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
