package com.mealmate.repository;

import com.mealmate.model.PickupRequest;
import com.mealmate.model.User;
import com.mealmate.model.FoodListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    List<PickupRequest> findByConsumer(User consumer);

    List<PickupRequest> findByFoodListingProvider(User provider);

    List<PickupRequest> findByFoodListing(FoodListing foodListing);
}
