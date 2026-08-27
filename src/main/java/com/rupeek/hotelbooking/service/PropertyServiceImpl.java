package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.exception.NotFoundException;
import com.rupeek.hotelbooking.repository.OwnerRepository;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository, OwnerRepository ownerRepository) {
        this.propertyRepository = propertyRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Property addProperty(String ownerId, String name, Location location, Set<Amenity> amenities, double starRating) {
        if (!ownerRepository.existsById(ownerId)) {
            throw new NotFoundException("Owner not found: " + ownerId);
        }
        Property property = new Property(UUID.randomUUID().toString(), ownerId, name, location, amenities, starRating);
        return propertyRepository.save(property);
    }

    @Override
    public RoomType addRoomType(String propertyId, String name, Money pricePerNight, int maxGuestsPerRoom, int totalRooms) {
        Property property = getProperty(propertyId);
        RoomType roomType = new RoomType(UUID.randomUUID().toString(), propertyId, name, pricePerNight, maxGuestsPerRoom, totalRooms);
        property.addRoomType(roomType);
        return roomType;
    }

    @Override
    public Property getProperty(String propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found: " + propertyId));
    }

    @Override
    public List<Property> listPropertiesForOwner(String ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }
}
