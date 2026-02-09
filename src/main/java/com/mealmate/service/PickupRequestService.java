package com.mealmate.service;

import com.mealmate.model.PickupRequest;
import com.mealmate.model.FoodListing;
import com.mealmate.model.User;
import com.mealmate.model.Notification;
import com.mealmate.repository.PickupRequestRepository;
import com.mealmate.repository.FoodListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PickupRequestService {

    private final PickupRequestRepository pickupRequestRepository;
    private final FoodListingRepository foodListingRepository;
    private final NotificationService notificationService;

    public PickupRequestService(PickupRequestRepository pickupRequestRepository,
            FoodListingRepository foodListingRepository,
            NotificationService notificationService) {
        this.pickupRequestRepository = pickupRequestRepository;
        this.foodListingRepository = foodListingRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void createRequest(Long foodListingId, User consumer, Integer quantity,
            PickupRequest.PaymentType paymentType, LocalDateTime pickupTime, String remarks) {
        if (foodListingId == null)
            throw new IllegalArgumentException("ID cannot be null");
        FoodListing listing = foodListingRepository.findById(foodListingId)
                .orElseThrow(() -> new RuntimeException("Food listing not found"));

        PickupRequest request = new PickupRequest();
        request.setFoodListing(listing);
        request.setConsumer(consumer);
        request.setQuantity(quantity);
        request.setPaymentType(paymentType);
        request.setRequestTime(LocalDateTime.now());
        request.setPickupTime(pickupTime);
        request.setRemarks(remarks);
        request.setStatus(PickupRequest.RequestStatus.PENDING);

        pickupRequestRepository.save(request);

        // Notify Provider
        notificationService.createNotification(
                listing.getProvider(),
                "New pickup request (" + quantity + " " + listing.getUnit() + ") from " + consumer.getName() + " for "
                        + listing.getItemName(),
                Notification.NotificationType.CLAIM);
    }

    @Transactional
    public void updateStatus(Long requestId, PickupRequest.RequestStatus status, String remarks) {
        PickupRequest request = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        request.setRemarks(remarks);
        pickupRequestRepository.save(request);

        // If approved, handle quantity deduction (Partial Claim Logic)
        if (status == PickupRequest.RequestStatus.APPROVED) {
            FoodListing listing = request.getFoodListing();

            if (request.getQuantity() >= listing.getQuantity()) {
                // Claim all
                listing.setStatus("Claimed");
                listing.setClaimedBy(request.getConsumer());
                listing.setPickupTime(request.getPickupTime());
                listing.setClaimerPhoneNumber(request.getConsumer().getPhoneNumber());
                foodListingRepository.save(listing);
            } else {
                // Partial claim - reduce original quantity
                listing.setQuantity(listing.getQuantity() - request.getQuantity());
                foodListingRepository.save(listing);
            }
        }

        // Notify Consumer
        Notification.NotificationType type = status == PickupRequest.RequestStatus.APPROVED
                ? Notification.NotificationType.APPROVAL
                : Notification.NotificationType.REJECTION;

        String message = "Your request for " + request.getQuantity() + " " + request.getFoodListing().getUnit() + " of "
                + request.getFoodListing().getItemName() + " has been " + status;

        if (status == PickupRequest.RequestStatus.APPROVED
                && request.getPaymentType() == PickupRequest.PaymentType.UPI) {
            message += ". Please proceed with UPI payment.";
        }

        notificationService.createNotification(
                request.getConsumer(),
                message,
                type);
    }

    @Transactional
    public void updatePaymentStatus(Long requestId, PickupRequest.PaymentStatus status) {
        PickupRequest request = pickupRequestRepository.findById(requestId).orElseThrow();
        request.setPaymentStatus(status);
        pickupRequestRepository.save(request);

        if (status == PickupRequest.PaymentStatus.PAID) {
            notificationService.createNotification(
                    request.getFoodListing().getProvider(),
                    "Payment received for " + request.getFoodListing().getItemName() + " from "
                            + request.getConsumer().getName(),
                    Notification.NotificationType.SYSTEM);
        }
    }

    public List<PickupRequest> getProviderRequests(User provider) {
        return pickupRequestRepository.findByFoodListingProvider(provider);
    }

    public List<PickupRequest> getConsumerRequests(User consumer) {
        return pickupRequestRepository.findByConsumer(consumer);
    }
}
