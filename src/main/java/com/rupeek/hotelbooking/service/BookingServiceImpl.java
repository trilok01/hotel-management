package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.exception.NotFoundException;
import com.rupeek.hotelbooking.exception.RoomUnavailableException;
import com.rupeek.hotelbooking.exception.ValidationException;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final RoomInventoryManager inventoryManager;

    public BookingServiceImpl(PropertyRepository propertyRepository,
                               BookingRepository bookingRepository,
                               RoomInventoryManager inventoryManager) {
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public Booking book(String propertyId, String roomTypeId, String guestId, DateRange dates, int numGuests, int numRooms) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found: " + propertyId));
        RoomType roomType = property.findRoomType(roomTypeId)
                .orElseThrow(() -> new NotFoundException("Room type not found: " + roomTypeId + " on property " + propertyId));

        if (numGuests > (long) roomType.getMaxGuestsPerRoom() * numRooms) {
            throw new ValidationException("Room type " + roomType.getName() + " cannot accommodate "
                    + numGuests + " guests across " + numRooms + " room(s)");
        }

        boolean reserved = inventoryManager.tryReserve(roomTypeId, dates, numRooms, roomType.getTotalRooms());
        if (!reserved) {
            throw new RoomUnavailableException(
                    "Room type " + roomType.getName() + " is not available for " + dates);
        }

        Money totalAmount = roomType.getBasePricePerNight().multiply(dates.nights()).multiply(numRooms);
        Booking booking = new Booking(UUID.randomUUID().toString(), propertyId, roomTypeId, guestId,
                dates, numGuests, numRooms, totalAmount);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    }
}
