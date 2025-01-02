package com.example.controller;

import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Řídící třída (Controller) pro autentizaci a registraci uživatelů.
 * Poskytuje logiku pro zobrazení přihlašovacích, registračních a domovských stránek.
 */
@Controller
public class AuthController {
    
    private UserService userService;

    /**
     * Injektování služby UserService pomocí setteru.
     * Tato služba se stará o ukládání a hledání uživatelů.
     */
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Zobrazení domovské stránky při přístupu na root URL "/".
     * @return Vrací šablonu "index.html" (domovská stránka).
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    /**
     * Zobrazení přihlašovací stránky na URL "/login".
     * @return Vrací šablonu "login.html".
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Zobrazení registračního formuláře na URL "/register".
     * Vytváří nový objekt UserDto a přidává jej do modelu pro použití ve formuláři.
     * @param model Model sloužící k předání dat do šablony.
     * @return Vrací šablonu "register.html".
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    /**
     * Zpracování odeslaného registračního formuláře.
     * Validuje vstupy uživatele a kontroluje, zda již existuje účet s daným e-mailem.
     * @param userDto Data z registračního formuláře.
     * @param result Výsledky validace formuláře.
     * @param model Model pro předání dat zpět do šablony v případě chyby.
     * @return V případě chyby vrací zpět na registrační stránku. Jinak přesměrování na "/register?success".
     */
    @SuppressWarnings("null")
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "Účet s tímto e-mailem již existuje");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        // Uložení nového uživatele do databáze
        userService.saveUser(userDto);
        return "redirect:/register?success";
    }
}
