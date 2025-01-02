package com.example.repository;

import com.example.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA repozitář pro entitu Reservation.
 * Poskytuje metody pro práci s rezervacemi v databázi.
 * Dědí z JpaRepository, což automaticky přidává základní CRUD operace.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Vyhledá rezervace podle ID uživatele.
     * @param userId ID uživatele, jehož rezervace se hledají.
     * @return Seznam rezervací patřících uživateli s daným ID.
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * Vyhledá rezervace na základě data rezervace.
     * @param reservationDate Datum, pro které se hledají rezervace.
     * @return Seznam rezervací vytvořených na daný den.
     */
    List<Reservation> findByReservationDate(LocalDate reservationDate);

    /**
     * Vyhledá rezervace na základě data a názvu sportoviště.
     * @param reservationDate Datum rezervace.
     * @param title Název sportoviště (např. "Tělocvična" nebo "Bazén").
     * @return Seznam rezervací pro dané sportoviště a datum.
     */
    List<Reservation> findByReservationDateAndTitle(LocalDate reservationDate, String title);
}
