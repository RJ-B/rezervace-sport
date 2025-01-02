package com.example.controller;

import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.service.ReservationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Kontroler pro správu rezervací uživatele.
 * Tento kontroler umožňuje přihlášenému uživateli vytvářet, upravovat a mazat rezervace.
 */
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final com.example.repository.UserRepository userRepository;

    /**
     * Konstruktor pro injektování služby rezervací a uživatelského repozitáře.
     */
    public ReservationController(ReservationService reservationService, com.example.repository.UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    /**
     * Zobrazení rezervací pro aktuálně přihlášeného uživatele.
     * @param model Model pro předání dat do šablony.
     * @param userDetails Detaily o přihlášeném uživateli.
     * @return Vrací šablonu "reservations.html" se seznamem rezervací a dostupnými časovými sloty.
     */
    @GetMapping
    public String showReservations(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername());

            if (user != null) {
                // Přidání rezervací do modelu
                model.addAttribute("reservations", reservationService.getUserReservations(user));
            }
        }

        // Předání dostupných časových slotů pro zobrazení v šabloně
        List<String> timeSlots = reservationService.generateHalfHourSlots();
        model.addAttribute("timeSlots", timeSlots);

        return "reservations";
    }

    /**
     * Uložení nové rezervace.
     * @param title Název sportoviště.
     * @param date Datum rezervace.
     * @param startTime Čas začátku rezervace.
     * @param endTime Čas ukončení rezervace.
     * @param note Poznámka (volitelná).
     * @param userDetails Detaily o přihlášeném uživateli.
     * @return Přesměrování zpět na seznam rezervací.
     */
    @PostMapping("/save")
    public String saveReservation(@RequestParam String title,
                                  @RequestParam String date,
                                  @RequestParam String startTime,
                                  @RequestParam String endTime,
                                  @RequestParam(required = false) String note,
                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                  Model model) {
    
        if (userDetails == null) {
            return "redirect:/login";
        }
    
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
    
        // Parsování data a času
        LocalDate reservationDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
    
        // Validace - začátek musí být před koncem
        if (start.isAfter(end) || start.equals(end)) {
            model.addAttribute("error", "Čas začátku musí být dříve než čas ukončení.");
            model.addAttribute("timeSlots", reservationService.generateHalfHourSlots());
            model.addAttribute("reservations", reservationService.getUserReservations(user));
            return "reservations";
        }
    
        // Uložení rezervace
        reservationService.saveReservation(
                title,
                reservationDate.atTime(start),
                reservationDate.atTime(end),
                note,
                user
        );
    
        return "redirect:/reservations";
    }
    

    /**
     * Načtení dostupných časových slotů pro vybraný den a sportoviště.
     * @param title Název sportoviště.
     * @param date Datum rezervace.
     * @param model Model pro předání dat do šablony.
     * @return Fragment šablony "availableSlots" s volnými sloty.
     */
    @GetMapping("/availableSlots")
    public String getAvailableSlots(@RequestParam String title, @RequestParam String date, Model model) {
        LocalDate reservationDate = LocalDate.parse(date);
        List<String> availableSlots = reservationService.getAvailableSlots(title, reservationDate);

        model.addAttribute("availableSlots", availableSlots);
        return "reservations :: availableSlots";
    }

    /**
     * Smazání rezervace podle ID.
     * Používá POST metodu kvůli kompatibilitě s prohlížeči, které nemusí podporovat DELETE.
     * @param id ID rezervace k odstranění.
     * @return Přesměrování na seznam rezervací.
     */
    @PostMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/reservations";
    }

    /**
     * Zobrazení formuláře pro úpravu existující rezervace.
     * @param id ID rezervace k úpravě.
     * @param model Model pro předání dat do šablony.
     * @return Šablona pro úpravu rezervace.
     */
    @GetMapping("/reservations/edit/{id}")
    public String editReservation(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findReservationById(id);
        model.addAttribute("reservation", reservation);
        return "admin/editReservation";  // Správná cesta k šabloně
    }

    /**
     * Uložení aktualizované rezervace.
     * @param id ID rezervace.
     * @param updatedReservation Aktualizovaná rezervace.
     * @return Přesměrování zpět na seznam rezervací s potvrzením úspěchu.
     */
    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @RequestParam String startTime,
                                    @RequestParam String endTime,
                                    @ModelAttribute Reservation updatedReservation,
                                    Model model) {
    
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
    
        // Validace - začátek musí být před koncem
        if (start.isAfter(end) || start.equals(end)) {
            model.addAttribute("error", "Čas začátku musí být dříve než čas ukončení.");
            model.addAttribute("reservation", reservationService.findReservationById(id));
            return "admin/editReservation";
        }
    
        // Aktualizace rezervace
        reservationService.updateReservation(id, updatedReservation);
    
        return "redirect:/reservations?success";
    }
    
}
