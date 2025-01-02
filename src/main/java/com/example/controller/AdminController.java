package com.example.controller;

import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.service.ReservationService;
import com.example.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReservationService reservationService;
    private final UserService userService;

    public AdminController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    // Zobrazení admin panelu se seznamem rezervací
    @GetMapping
    public String showAdminPage(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<User> users = userService.findAllUsers().stream()
                .map(userDto -> {
                    User user = new User();
                    user.setEmail(userDto.getEmail());
                    user.setName(userDto.getFirstName() + " " + userDto.getLastName());
                    return user;
                }).toList();

        model.addAttribute("reservations", reservations);
        model.addAttribute("users", users);
        return "admin/admin";  // Odkaz na šablonu admin.html
    }

    // Mazání rezervace a přesměrování zpět na admin s úspěšnou zprávou
    @PostMapping("/reservations/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/admin?success";
    }

@PostMapping("/reservations/edit/{id}")
public String editReservation(@PathVariable Long id, Model model) {
    Reservation reservation = reservationService.findReservationById(id);
    model.addAttribute("editReservation", reservation);
    model.addAttribute("reservations", reservationService.getAllReservations());
    return "admin/admin";
}

@PostMapping("/reservations/update/{id}")
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
        model.addAttribute("editReservation", updatedReservation);
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/admin";
    }

    // Aktualizace rezervace
    reservationService.updateReservation(id, updatedReservation);

    return "redirect:/admin?updated";
}
}
