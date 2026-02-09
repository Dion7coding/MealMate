package com.mealmate.controller;

import com.mealmate.dto.FoodListingDto;
import com.mealmate.model.FoodListing;
import com.mealmate.model.User;
import com.mealmate.service.FoodListingService;
import com.mealmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final FoodListingService foodListingService;
    private final UserService userService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public DashboardController(FoodListingService foodListingService, UserService userService) {
        this.foodListingService = foodListingService;
        this.userService = userService;
    }

    @PostMapping("/update-upi")
    public String updateUpi(@RequestParam("upiFile") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        if (!file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                User updatedUser = userService.updateUpiQrCode(user.getId(), "/uploads/" + fileName);
                session.setAttribute("user", updatedUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        List<FoodListing> myListings = foodListingService.findByProvider(user);

        List<FoodListing> availableListings = myListings.stream()
                .filter(l -> "Available".equals(l.getStatus()))
                .toList();

        List<FoodListing> claimedListings = myListings.stream()
                .filter(l -> "Claimed".equals(l.getStatus()))
                .toList();

        model.addAttribute("availableListings", availableListings);
        model.addAttribute("claimedListings", claimedListings);
        model.addAttribute("user", user);
        model.addAttribute("totalItems", availableListings.size());

        return "dashboard/my-listings";
    }

    @GetMapping("/add")
    public String addListingPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";
        model.addAttribute("listingDto", new FoodListingDto());
        return "dashboard/add-listing";
    }

    @PostMapping("/add")
    public String addListing(@ModelAttribute FoodListingDto listingDto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        foodListingService.saveListing(listingDto, user);
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editListingPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        FoodListing listing = foodListingService.getListing(id);
        if (!listing.getProvider().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }

        FoodListingDto dto = new FoodListingDto();
        dto.setId(listing.getId());
        dto.setItemName(listing.getItemName());
        dto.setDescription(listing.getDescription());
        dto.setQuantity(listing.getQuantity());
        dto.setCategory(listing.getCategory());
        dto.setPickupLocation(listing.getPickupLocation());
        dto.setContactNumber(listing.getContactNumber());
        dto.setExpiryTimeStr(listing.getExpiryTime().toString());
        dto.setUnit(listing.getUnit());
        dto.setPrice(listing.getPrice());
        dto.setDiscountedPrice(listing.getDiscountedPrice());
        dto.setCurrency(listing.getCurrency());

        model.addAttribute("listingDto", dto);
        return "dashboard/add-listing";
    }

    @GetMapping("/delete/{id}")
    public String deleteListing(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        FoodListing listing = foodListingService.getListing(id);
        if (listing.getProvider().getId().equals(user.getId())) {
            foodListingService.deleteListing(id);
        }
        return "redirect:/dashboard";
    }
}
