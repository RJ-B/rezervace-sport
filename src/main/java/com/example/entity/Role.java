package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entita Role reprezentuje uživatelské role v systému.
 * Každý uživatel může mít jednu nebo více rolí.
 * Tato třída využívá Lombok pro automatické generování getterů, setterů
 * a konstruktorů.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")  // Nastavuje název tabulky v databázi na "roles"
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatické generování ID (primární klíč)
    private Long id;

    @Column(nullable = false, unique = true)  // Název role je povinný a musí být unikátní
    private String name;

    @ManyToMany(mappedBy = "roles")  // Mnoho rolí může být přiřazeno k mnoha uživatelům
    private List<User> users;
}
