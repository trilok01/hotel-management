package com.rupeek.hotelbooking.search.filters;

import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.search.PropertyFilter;
import com.rupeek.hotelbooking.search.SearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityFilter implements PropertyFilter {

    private final RoomInventoryManager inventoryManager;

    public AvailabilityFilter(RoomInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.getDates() == null) {
            return true;
        }
        for (RoomType roomType : property.getRoomTypes()) {
            int roomsRequired =
                    (criteria.getNumGuests()
                            + roomType.getMaxGuestsPerRoom() - 1)
                            / roomType.getMaxGuestsPerRoom();

            if (roomsRequired > roomType.getTotalRooms()) {
                continue;
            }

            boolean available = inventoryManager.isAvailable(
                    roomType.getId(),
                    criteria.getDates(),
                    roomsRequired,
                    roomType.getTotalRooms());
            if (available) {
                return true;
            }
        }
        return false;
    }
}
