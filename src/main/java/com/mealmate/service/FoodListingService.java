package com.mealmate.service;

import com.mealmate.dto.FoodListingDto;
import com.mealmate.model.FoodListing;
import com.mealmate.model.User;
import com.mealmate.repository.FoodListingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodListingService {

    private final FoodListingRepository foodListingRepository;

    public FoodListingService(FoodListingRepository foodListingRepository) {
        this.foodListingRepository = foodListingRepository;
    }

    public List<FoodListing> findAllAvailable() {
        return foodListingRepository.findByStatusAndExpiryTimeAfterOrderByExpiryTimeAsc("Available",
                LocalDateTime.now());
    }

    public List<FoodListing> findByProvider(User provider) {
        return foodListingRepository.findByProvider(provider);
    }

    public FoodListing saveListing(FoodListingDto dto, User provider) {
        FoodListing listing = new FoodListing();
        if (dto.getId() != null) {
            listing = foodListingRepository.findById(dto.getId()).orElse(new FoodListing());
        }
        listing.setItemName(dto.getItemName());
        listing.setDescription(dto.getDescription());
        listing.setQuantity(dto.getQuantity());
        listing.setUnit(dto.getUnit());
        listing.setPrice(dto.getPrice());
        listing.setDiscountedPrice(dto.getDiscountedPrice());
        listing.setCategory(dto.getCategory());
        listing.setPickupLocation(dto.getPickupLocation());
        listing.setContactNumber(dto.getContactNumber());
        listing.setCurrency(dto.getCurrency());
        listing.setProvider(provider);
        listing.setStatus("Available");
        listing.setClaimedBy(null);

        // Parse expiry time
        if (dto.getExpiryTimeStr() != null && !dto.getExpiryTimeStr().isEmpty()) {
            listing.setExpiryTime(LocalDateTime.parse(dto.getExpiryTimeStr()));
        }

        return foodListingRepository.save(listing);
    }

    public void deleteListing(Long id) {
        foodListingRepository.deleteById(id);
    }

    public void claimListing(Long id, User consumer, Integer claimQuantity, LocalDateTime pickupTime,
            String phoneNumber) {
        FoodListing listing = foodListingRepository.findById(id).orElseThrow();

        if (claimQuantity >= listing.getQuantity()) {
            // Claim all
            listing.setStatus("Claimed");
            listing.setClaimedBy(consumer);
            listing.setPickupTime(pickupTime);
            listing.setClaimerPhoneNumber(phoneNumber);
            foodListingRepository.save(listing);
        } else {
            // Partial claim - reduce original quantity
            listing.setQuantity(listing.getQuantity() - claimQuantity);
            foodListingRepository.save(listing);

            // Create new listing for claimed amount
            FoodListing claimedPart = new FoodListing();
            claimedPart.setItemName(listing.getItemName());
            claimedPart.setDescription(listing.getDescription());
            claimedPart.setQuantity(claimQuantity);
            claimedPart.setUnit(listing.getUnit());
            claimedPart.setPrice(listing.getPrice());
            claimedPart.setDiscountedPrice(listing.getDiscountedPrice());
            claimedPart.setCategory(listing.getCategory());
            claimedPart.setPickupLocation(listing.getPickupLocation());
            claimedPart.setContactNumber(listing.getContactNumber());
            claimedPart.setProvider(listing.getProvider());
            claimedPart.setExpiryTime(listing.getExpiryTime());
            claimedPart.setCurrency(listing.getCurrency());

            claimedPart.setStatus("Claimed");
            claimedPart.setClaimedBy(consumer);
            claimedPart.setPickupTime(pickupTime);
            claimedPart.setClaimerPhoneNumber(phoneNumber);

            foodListingRepository.save(claimedPart);
        }
    }

    public List<FoodListing> findClaimedBy(User user) {
        return foodListingRepository.findAll().stream()
                .filter(l -> l.getClaimedBy() != null && l.getClaimedBy().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public void togglePickedUpStatus(Long id) {
        FoodListing listing = foodListingRepository.findById(id).orElseThrow();
        listing.setPickedUp(!listing.isPickedUp());
        foodListingRepository.save(listing);
    }

    public FoodListing getListing(Long id) {
        return foodListingRepository.findById(id).orElseThrow();
    }
}
