package com.rupeek.hotelbooking.domain.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Property {

    private final String id;
    private final String ownerId;
    private String name;
    private Location location;
    private Set<Amenity> amenities;
    private double starRating;
    private final List<RoomType> roomTypes = new CopyOnWriteArrayList<>();

    public Property(String id, String ownerId, String name, Location location,
                     Set<Amenity> amenities, double starRating) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
        this.amenities = amenities == null ? Collections.emptySet() : amenities;
        if (starRating < 0 || starRating > 5) {
            throw new IllegalArgumentException("starRating must be between 0 and 5");
        }
        this.starRating = starRating;
    }

    public void addRoomType(RoomType roomType) {
        roomTypes.add(roomType);
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public double getStarRating() {
        return starRating;
    }

    public List<RoomType> getRoomTypes() {
        return new ArrayList<>(roomTypes);
    }

    public java.util.Optional<RoomType> findRoomType(String roomTypeId) {
        return roomTypes.stream().filter(rt -> rt.getId().equals(roomTypeId)).findFirst();
    }

    /** Cheapest nightly rate across room types - useful for search result sorting/display. */
    public java.util.Optional<com.rupeek.hotelbooking.domain.common.Money> cheapestRate() {
        return roomTypes.stream()
                .map(RoomType::getBasePricePerNight)
                .min((a, b) -> a.getAmount().compareTo(b.getAmount()));
    }
}
