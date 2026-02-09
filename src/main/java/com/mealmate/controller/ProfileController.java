package com.mealmate.controller;

import com.mealmate.model.User;
import com.mealmate.service.FoodListingService;
import com.mealmate.service.PickupRequestService;
import com.mealmate.service.UserService;
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
    private final UserService userService;
    private final PickupRequestService pickupRequestService;

    public ProfileController(FoodListingService foodListingService, UserService userService,
            PickupRequestService pickupRequestService) {
        this.foodListingService = foodListingService;
        this.userService = userService;
        this.pickupRequestService = pickupRequestService;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Refresh user from DB
        user = userService.findById(user.getId());
        session.setAttribute("user", user);

        model.addAttribute("user", user);
        model.addAttribute("claims", foodListingService.findClaimedBy(user));
        model.addAttribute("requests", pickupRequestService.getConsumerRequests(user));
        return "profile";
    }

    @PostMapping("/update-details")
    public String updateDetails(@RequestParam String phoneNumber,
            @RequestParam String address,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        User updatedUser = userService.updateProfile(user.getId(), phoneNumber, address);
        session.setAttribute("user", updatedUser);
        return "redirect:/profile?success=true";
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
}
