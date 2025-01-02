package com.example.service;

import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.repository.ReservationRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servisní třída pro správu rezervací.
 * Zajišťuje veškeré CRUD operace nad entitou Reservation.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * Konstruktor pro injektování repozitáře rezervací.
     */
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Získání všech rezervací pro konkrétního uživatele.
     * @param user Uživatel, jehož rezervace se načítají.
     * @return Seznam rezervací daného uživatele.
     */
    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUserId(user.getId());
    }

    /**
     * Generování dostupných půlhodinových časových slotů od 8:00 do 20:00.
     * @return Seznam časových slotů ve formátu HH:mm.
     */
    public List<String> generateHalfHourSlots() {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);

        while (start.isBefore(end)) {
            slots.add(start.toString());
            start = start.plusMinutes(30);
        }
        return slots;
    }

    /**
     * Získání dostupných časových slotů pro daný den a sportoviště.
     * @param title Název sportoviště.
     * @param reservationDate Datum rezervace.
     * @return Seznam dostupných časových slotů.
     */
    public List<String> getAvailableSlots(String title, LocalDate reservationDate) {
        List<String> allSlots = generateHalfHourSlots();

        // Najdeme existující rezervace na dané datum a sportoviště
        List<Reservation> existingReservations = reservationRepository.findByReservationDateAndTitle(reservationDate, title);

        // Odstranění obsazených časů
        for (Reservation reservation : existingReservations) {
            LocalTime startTime = reservation.getStartTime().toLocalTime();
            allSlots.remove(startTime.toString());
        }

        return allSlots;
    }

    /**
     * Uložení nové rezervace.
     * @param title Název sportoviště.
     * @param startTime Datum a čas začátku rezervace.
     * @param endTime Datum a čas konce rezervace.
     * @param note Poznámka (volitelná).
     * @param user Uživatel, který rezervaci vytváří.
     */
    public void saveReservation(String title, LocalDateTime startTime, LocalDateTime endTime, String note, User user) {
        Reservation reservation = new Reservation();
        reservation.setTitle(title);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setNote(note);
        reservation.setUser(user);

        reservationRepository.save(reservation);
    }

    /**
     * Smazání rezervace podle ID.
     * @param id ID rezervace k odstranění.
     */
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    /**
     * Získání všech rezervací (pro admina).
     * @return Seznam všech rezervací.
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Získání všech rezervací seřazených podle názvu sportoviště.
     * @return Seznam rezervací seřazený vzestupně podle názvu.
     */
    public List<Reservation> getAllReservationsSortedByTitle() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

    /**
     * Nalezení konkrétní rezervace podle jejího ID.
     * @param id ID rezervace.
     * @return Rezervace nebo null, pokud rezervace neexistuje.
     */
    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    /**
     * Aktualizace existující rezervace.
     * @param id ID rezervace.
     * @param updatedReservation Objekt s aktualizovanými daty rezervace.
     */
    public void updateReservation(Long id, Reservation updatedReservation) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);

        if (existingReservation != null) {
            existingReservation.setTitle(updatedReservation.getTitle());
            existingReservation.setReservationDate(updatedReservation.getReservationDate());
            existingReservation.setStartTime(updatedReservation.getStartTime());
            existingReservation.setEndTime(updatedReservation.getEndTime());
            existingReservation.setNote(updatedReservation.getNote());

            reservationRepository.save(existingReservation);
        }
    }
}
