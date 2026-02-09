package com.mealmate.service;

import com.mealmate.model.Feedback;
import com.mealmate.model.FoodListing;
import com.mealmate.model.User;
import com.mealmate.repository.FeedbackRepository;
import com.mealmate.repository.FoodListingRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FoodListingRepository foodListingRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, FoodListingRepository foodListingRepository) {
        this.feedbackRepository = feedbackRepository;
        this.foodListingRepository = foodListingRepository;
    }

    public void submitFeedback(Long foodListingId, User consumer, Integer rating, String comment) {
        FoodListing listing = foodListingRepository.findById(foodListingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Strict requirement check
        if (!listing.isPickedUp()) {
            throw new IllegalStateException("Feedback can only be provided after pickup is completed.");
        }

        Feedback feedback = new Feedback();
        feedback.setFoodListing(listing);
        feedback.setProvider(listing.getProvider());
        feedback.setConsumer(consumer);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
    }

    public List<Feedback> getProviderFeedback(User provider) {
        return feedbackRepository.findByProvider(provider);
    }
}
