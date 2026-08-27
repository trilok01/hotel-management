package com.rupeek.hotelbooking.controller;

import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.dto.PropertyRequest;
import com.rupeek.hotelbooking.dto.RoomTypeRequest;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<Property> addProperty(@Valid @RequestBody PropertyRequest request) {
        Set<Amenity> amenities = request.amenities() == null
                ? Set.of()
                : request.amenities().stream().map(Amenity::of).collect(Collectors.toSet());
        Location location = new Location(request.city(), request.locality(), request.address());
        Property property = propertyService.addProperty(
                request.ownerId(), request.name(), location, amenities, request.starRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(property);
    }

    @PostMapping("/{propertyId}/room-types")
    public ResponseEntity<RoomType> addRoomType(@PathVariable String propertyId,
                                                 @Valid @RequestBody RoomTypeRequest request) {
        RoomType roomType = propertyService.addRoomType(propertyId, request.name(),
                new Money(request.pricePerNight()), request.maxGuestsPerRoom(), request.totalRooms());
        return ResponseEntity.status(HttpStatus.CREATED).body(roomType);
    }

    @GetMapping("/{propertyId}")
    public Property get(@PathVariable String propertyId) {
        return propertyService.getProperty(propertyId);
    }

    @GetMapping
    public List<Property> listByOwner(@RequestParam String ownerId) {
        return propertyService.listPropertiesForOwner(ownerId);
    }
}
