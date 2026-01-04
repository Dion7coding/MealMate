package com.mealmate.controller;

import com.mealmate.model.Role;
import com.mealmate.model.User;
import com.mealmate.service.FoodListingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;

@Controller
public class MarketplaceController {

    private final FoodListingService foodListingService;

    public MarketplaceController(FoodListingService foodListingService) {
        this.foodListingService = foodListingService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole() == Role.PROVIDER) {
            return "redirect:/dashboard";
        }

        model.addAttribute("listings", foodListingService.findAllAvailable());
        return "index";
    }

    @PostMapping("/claim")
    public String claim(@RequestParam Long id,
            @RequestParam Integer quantity,
            @RequestParam String pickupTime,
            @RequestParam String phoneNumber,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole() == Role.PROVIDER) {
            return "redirect:/dashboard";
        }

        try {
            LocalDateTime time = LocalDateTime.parse(pickupTime);
            foodListingService.claimListing(id, user, quantity, time, phoneNumber);
        } catch (Exception e) {
            // Handle parsing error or service error
            return "redirect:/?error=claim_failed";
        }
        return "redirect:/?claimed=true";
    }
}
