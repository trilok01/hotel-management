package com.rupeek.hotelbooking.domain.property;

import com.rupeek.hotelbooking.domain.common.Money;

import java.util.Objects;

public class RoomType {

    private final String id;
    private final String propertyId;
    private String name;
    private Money basePricePerNight;
    private int maxGuestsPerRoom;
    private int totalRooms;

    public RoomType(String id, String propertyId, String name, Money basePricePerNight,
                     int maxGuestsPerRoom, int totalRooms) {
        this.id = Objects.requireNonNull(id);
        this.propertyId = Objects.requireNonNull(propertyId);
        this.name = Objects.requireNonNull(name);
        this.basePricePerNight = Objects.requireNonNull(basePricePerNight);
        if (maxGuestsPerRoom <= 0) {
            throw new IllegalArgumentException("maxGuestsPerRoom must be positive");
        }
        if (totalRooms <= 0) {
            throw new IllegalArgumentException("totalRooms must be positive");
        }
        this.maxGuestsPerRoom = maxGuestsPerRoom;
        this.totalRooms = totalRooms;
    }

    public String getId() {
        return id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getName() {
        return name;
    }

    public Money getBasePricePerNight() {
        return basePricePerNight;
    }

    public int getMaxGuestsPerRoom() {
        return maxGuestsPerRoom;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBasePricePerNight(Money basePricePerNight) {
        this.basePricePerNight = basePricePerNight;
    }
}
