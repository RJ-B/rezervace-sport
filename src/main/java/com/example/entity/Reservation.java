package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entita představující rezervaci sportoviště.
 * Tato třída je mapována na tabulku "reservations" v databázi.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primární klíč, automaticky generovaný (auto-increment)

    @Column(nullable = false)
    private String title;  // Název rezervovaného sportoviště (např. tělocvična, bazén)

    @Column(nullable = false)
    private LocalDateTime startTime;  // Čas začátku rezervace

    @Column(nullable = false)
    private LocalDateTime endTime;  // Čas ukončení rezervace

    @Column(nullable = false)
    private LocalDate reservationDate;  // Datum rezervace, automaticky odvozeno ze startTime

    @Column
    private String note;  // Volitelná poznámka k rezervaci

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Vztah k uživateli, který rezervaci vytvořil

    /**
     * Metoda pro automatickou aktualizaci reservationDate při nastavení startTime.
     * Tato metoda je volána před uložením (persist) nebo aktualizací (update) entity.
     */
    @PrePersist
    @PreUpdate
    public void updateReservationDate() {
        if (this.startTime != null) {
            this.reservationDate = this.startTime.toLocalDate();
        }
    }
}
