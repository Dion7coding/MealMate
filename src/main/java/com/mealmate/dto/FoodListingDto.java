package com.mealmate.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FoodListingDto {
    private Long id;
    private String itemName;
    private String description;
    private Integer quantity;
    private String unit;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String category;
    private String expiryTimeStr; // Using String for form simplicity, will parse in service
    private String pickupLocation;
    private String contactNumber;
    private String currency;
}
