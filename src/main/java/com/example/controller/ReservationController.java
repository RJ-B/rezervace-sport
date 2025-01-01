package com.example.controller;

import com.example.entity.User;
import com.example.service.ReservationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @GetMapping
    public String showReservations(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails != null) {
            System.out.println("Uživatel na stránce rezervací: " + userDetails.getUsername());
            
            // Najdi entitu User na základě emailu z UserDetails
            User user = userRepository.findByEmail(userDetails.getUsername());
    
            if (user != null) {
                model.addAttribute("reservations", reservationService.getUserReservations(user));
            } else {
                System.out.println("Uživatel nebyl nalezen v databázi.");
                model.addAttribute("message", "Uživatel nenalezen.");
            }
        } else {
            System.out.println("Žádný uživatel na stránce rezervací.");
            model.addAttribute("message", "Pro zobrazení rezervací se musíte přihlásit.");
        }
        return "reservations";  // Thymeleaf šablona
    }
    
    
    @Autowired
    private com.example.repository.UserRepository userRepository;  // Přidej UserRepository


@PostMapping("/save")
public String saveReservation(@RequestParam String title,
                              @RequestParam String date,
                              @RequestParam String time,
                              @RequestParam int duration,
                              @RequestParam(required = false) String note,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
    System.out.println("Ukládám rezervaci...");
    System.out.println("Název: " + title);
    System.out.println("Datum: " + date);
    System.out.println("Čas: " + time);
    System.out.println("Délka: " + duration);

    if (userDetails == null) {
        System.out.println("Uživatel není přihlášen. Přesměrování na login.");
        return "redirect:/login";
    }

    // Najdi entitu User na základě emailu z UserDetails
    User user = userRepository.findByEmail(userDetails.getUsername());

    if (user == null) {
        System.out.println("Uživatel nebyl nalezen v databázi.");
        return "redirect:/login";
    }

    LocalDateTime reservationDateTime = LocalDateTime.parse(date + "T" + time);
    reservationService.saveReservation(title, reservationDateTime, duration, note, user);

    return "redirect:/reservations";
}

    
    

    // Smazání rezervace
    @PostMapping("/delete")
    public String deleteReservation(@RequestParam Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/reservations";
    }
}
