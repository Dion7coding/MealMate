package com.mealmate.controller;

import com.mealmate.model.User;
import com.mealmate.model.PickupRequest;
import com.mealmate.service.PickupRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/requests")
public class PickupRequestController {

    private final PickupRequestService pickupRequestService;

    public PickupRequestController(PickupRequestService pickupRequestService) {
        this.pickupRequestService = pickupRequestService;
    }

    @PostMapping("/create")
    public String createRequest(@RequestParam Long foodListingId,
            @RequestParam Integer quantity,
            @RequestParam String paymentType,
            @RequestParam String pickupTime,
            @RequestParam(required = false) String remarks,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        pickupRequestService.createRequest(foodListingId, user, quantity,
                PickupRequest.PaymentType.valueOf(paymentType),
                LocalDateTime.parse(pickupTime), remarks);
        return "redirect:/?request_success=true";
    }

    @GetMapping("/provider")
    public String viewProviderRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().name().equals("PROVIDER"))
            return "redirect:/login";

        model.addAttribute("requests", pickupRequestService.getProviderRequests(user));
        return "requests/provider-list";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().name().equals("PROVIDER"))
            return "redirect:/login";

        pickupRequestService.updateStatus(requestId, PickupRequest.RequestStatus.valueOf(status), remarks);
        return "redirect:/requests/provider";
    }

    @PostMapping("/update-payment")
    public String updatePaymentStatus(@RequestParam Long requestId,
            @RequestParam String status,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        pickupRequestService.updatePaymentStatus(requestId, PickupRequest.PaymentStatus.valueOf(status));
        return "redirect:/profile";
    }
}
