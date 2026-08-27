package com.rupeek.hotelbooking.search;

import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryPropertyRepository;
import com.rupeek.hotelbooking.search.filters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertySearchServiceTest {

    private PropertyRepository propertyRepository;
    private RoomInventoryManager inventoryManager;
    private PropertySearchService searchService;

    @BeforeEach
    void setUp() {
        propertyRepository = new InMemoryPropertyRepository();
        inventoryManager = new RoomInventoryManager();

        Property cheapBlr = new Property("p1", "owner-1", "Budget Stay",
                new Location("Bengaluru", "HSR", "addr"), Set.of(Amenity.WIFI), 3.0);
        cheapBlr.addRoomType(new RoomType("rt1", "p1", "Standard", Money.of(1500), 2, 5));
        propertyRepository.save(cheapBlr);

        Property luxuryBlr = new Property("p2", "owner-1", "Luxury Stay",
                new Location("Bengaluru", "Indiranagar", "addr"), Set.of(Amenity.WIFI, Amenity.POOL, Amenity.SPA), 4.8);
        luxuryBlr.addRoomType(new RoomType("rt2", "p2", "Suite", Money.of(9000), 3, 2));
        propertyRepository.save(luxuryBlr);

        Property mumbaiProperty = new Property("p3", "owner-2", "Sea View",
                new Location("Mumbai", "Bandra", "addr"), Set.of(Amenity.WIFI), 4.0);
        mumbaiProperty.addRoomType(new RoomType("rt3", "p3", "Standard", Money.of(3000), 2, 5));
        propertyRepository.save(mumbaiProperty);

        List<PropertyFilter> filters = List.of(
                new CityFilter(), new PriceRangeFilter(), new AmenityFilter(),
                new StarRatingFilter(), new AvailabilityFilter(inventoryManager));
        searchService = new PropertySearchService(propertyRepository, filters);
    }

    @Test
    void filtersByCity() {
        var results = searchService.search(SearchCriteria.builder().city("Bengaluru").build());
        assertEquals(2, results.size());
    }

    @Test
    void filtersByPriceRange() {
        var results = searchService.search(SearchCriteria.builder()
                .city("Bengaluru").priceRange(null, Money.of(2000)).build());
        assertEquals(1, results.size());
        assertEquals("Budget Stay", results.get(0).getName());
    }

    @Test
    void filtersByRequiredAmenities() {
        var results = searchService.search(SearchCriteria.builder()
                .requiredAmenities(Set.of(Amenity.POOL)).build());
        assertEquals(1, results.size());
        assertEquals("Luxury Stay", results.get(0).getName());
    }

    @Test
    void filtersByMinStarRating() {
        var results = searchService.search(SearchCriteria.builder().minStarRating(4.5).build());
        assertEquals(1, results.size());
        assertEquals("Luxury Stay", results.get(0).getName());
    }

    @Test
    void excludesPropertiesWithNoAvailabilityForRequestedDates() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        // Fully book Budget Stay's only room type.
        assertTrue(inventoryManager.tryReserve("rt1", dates, 5, 5));

        var results = searchService.search(SearchCriteria.builder().city("Bengaluru").dates(dates).build());
        assertEquals(1, results.size());
        assertEquals("Luxury Stay", results.get(0).getName());
    }
}
