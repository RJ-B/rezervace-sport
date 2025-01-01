package com.example.service;

import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.repository.ReservationRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Získání rezervací pro konkrétního uživatele
    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUserId(user.getId());
    }

    public void saveReservation(String title, LocalDateTime dateTime, int duration, String note, User user) {
        System.out.println("Pokouším se uložit rezervaci...");
        System.out.println("Název: " + title);
        System.out.println("Datum a čas: " + dateTime);
        System.out.println("Délka (h): " + duration);
        System.out.println("Poznámka: " + note);
        System.out.println("Uživatel: " + (user != null ? user.getEmail() : "Nepřihlášen"));
    
        Reservation reservation = new Reservation();
        reservation.setTitle(title);
        reservation.setReservationDate(dateTime);
        reservation.setDuration(duration);
        reservation.setNote(note);
        reservation.setUser(user);
    
        reservationRepository.save(reservation);
        System.out.println("Rezervace byla uložena s ID: " + reservation.getId());
    }

    // Smazání rezervace podle ID
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();  // Vrátí všechny rezervace
    }  
}
