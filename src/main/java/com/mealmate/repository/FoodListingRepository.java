package com.mealmate.repository;

import com.mealmate.model.FoodListing;
import com.mealmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {
    List<FoodListing> findByProvider(User provider);

    List<FoodListing> findByStatusOrderByExpiryTimeAsc(String status);

    List<FoodListing> findByStatusAndExpiryTimeAfterOrderByExpiryTimeAsc(String status, LocalDateTime time);
}
