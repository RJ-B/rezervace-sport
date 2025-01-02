package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA repozitář pro entitu User.
 * Poskytuje metody pro práci s uživateli v databázi.
 * Dědí z JpaRepository, což automaticky přidává základní CRUD operace.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Vyhledá uživatele podle emailu.
     * @param email Email uživatele.
     * @return Uživatel odpovídající danému emailu, pokud existuje.
     */
    User findByEmail(String email);

        // Načte uživatele i s jeho rolemi
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    User findByEmailWithRoles(@Param("email") String email);
}
