package com.mealmate.controller;

import com.mealmate.model.User;
import com.mealmate.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/submit")
    public String submitFeedback(@RequestParam Long foodListingId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            feedbackService.submitFeedback(foodListingId, user, rating, comment);
            return "redirect:/profile?feedback_success=true";
        } catch (IllegalStateException e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }
}
