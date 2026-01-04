package com.mealmate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "food_listings")
public class FoodListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String category; // Cooked Food, Groceries, Bakery, Packaged

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String status = "Available"; // Available, Claimed

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Column
    private String unit; // e.g. "kg", "portion", "pack"

    @Column
    private String currency = "USD"; // Default to USD

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by")
    private User claimedBy;

    // Phase 4: Quality of Life Updates
    private LocalDateTime pickupTime;

    private String claimerPhoneNumber;

    private boolean isPickedUp = false;
}
