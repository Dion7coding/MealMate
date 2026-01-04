package com.mealmate.controller;

import com.mealmate.dto.LoginDto;
import com.mealmate.dto.UserRegistrationDto;
import com.mealmate.model.User;
import com.mealmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, HttpSession session, Model model) {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());
        if (user != null) {
            session.setAttribute("user", user);
            if (user.getRole() == com.mealmate.model.Role.PROVIDER) {
                return "redirect:/dashboard";
            } else {
                return "redirect:/";
            }
        }
        model.addAttribute("error", "Invalid credentials");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationDto userDto, HttpSession session) {
        User user = userService.register(userDto);
        session.setAttribute("user", user);
        if (user.getRole() == com.mealmate.model.Role.PROVIDER) {
            return "redirect:/dashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
