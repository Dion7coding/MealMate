package com.mealmate.controller;

import com.mealmate.model.User;
import com.mealmate.service.FoodListingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final FoodListingService foodListingService;

    public ProfileController(FoodListingService foodListingService) {
        this.foodListingService = foodListingService;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Refresh user from DB to get latest details (like new address if updated)
        // Ignoring for now since we store in session, but ideally we should refresh.
        // Let's assume session user is updated or we re-fetch.

        model.addAttribute("user", user);
        model.addAttribute("claims", foodListingService.findClaimedBy(user));
        return "profile"; // Thymeleaf template
    }

    @PostMapping("/toggle-pickup/{id}")
    public String togglePickup(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        foodListingService.togglePickedUpStatus(id);
        return "redirect:/profile";
    }

    // Placeholder for update/reset password if needed
}
