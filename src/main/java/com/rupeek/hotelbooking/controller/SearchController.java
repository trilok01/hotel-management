package com.rupeek.hotelbooking.controller;

import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.search.PropertySearchService;
import com.rupeek.hotelbooking.search.SearchCriteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    private final PropertySearchService searchService;

    public SearchController(PropertySearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public List<Property> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "1") int guests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Set<String> amenities,
            @RequestParam(required = false) Double minStarRating) {

        SearchCriteria.Builder builder = SearchCriteria.builder()
                .city(city)
                .numGuests(guests)
                .priceRange(minPrice == null ? null : new Money(minPrice), maxPrice == null ? null : new Money(maxPrice))
                .minStarRating(minStarRating)
                .requiredAmenities(amenities == null ? Set.of()
                        : amenities.stream().map(Amenity::of).collect(Collectors.toSet()));

        if (checkIn != null && checkOut != null) {
            builder.dates(new DateRange(checkIn, checkOut));
        }

        return searchService.search(builder.build());
    }
}
