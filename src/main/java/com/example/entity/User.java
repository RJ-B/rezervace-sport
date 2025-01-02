package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entita User reprezentuje uživatele v systému.
 * Uživatel má základní informace jako jméno, e-mail, heslo a seznam rolí.
 * Používá Lombok pro automatické generování getterů, setterů a konstruktorů.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")  // Určuje název tabulky v databázi
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatické generování ID
    private Long id;

    @Column(nullable = false)  // Jméno uživatele je povinné
    private String name;

    @Column(nullable = false, unique = true)  // E-mail musí být unikátní a povinný
    private String email;

    @Column(nullable = false)  // Heslo je povinné
    private String password;

    /**
     * Mnoho-na-mnoho vztah mezi uživateli a rolemi.
     * FetchType.EAGER = role jsou načteny hned s uživatelem (bez lazy loadingu).
     * CascadeType.ALL = všechny operace (persist, merge, remove) se propagují na roli.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",  // Spojovací tabulka
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},  // FK na uživatele
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})  // FK na roli
    private List<Role> roles = new ArrayList<>();  // Výchozí prázdný seznam rolí
}
