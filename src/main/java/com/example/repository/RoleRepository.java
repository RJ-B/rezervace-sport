package com.example.repository;

import com.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repozitář pro entitu Role.
 * Poskytuje metody pro práci s rolemi v databázi.
 * Dědí z JpaRepository, což automaticky přidává základní CRUD operace.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Vyhledá roli podle názvu.
     * @param name Název role (např. ROLE_ADMIN, ROLE_USER).
     * @return Role odpovídající danému názvu, pokud existuje.
     */
    Role findByName(String name);
}
