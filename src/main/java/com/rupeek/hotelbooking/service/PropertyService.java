package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;

import java.util.List;
import java.util.Set;

public interface PropertyService {

    Property addProperty(String ownerId, String name, Location location, Set<Amenity> amenities, double starRating);

    RoomType addRoomType(String propertyId, String name, Money pricePerNight, int maxGuestsPerRoom, int totalRooms);

    Property getProperty(String propertyId);

    List<Property> listPropertiesForOwner(String ownerId);
}
