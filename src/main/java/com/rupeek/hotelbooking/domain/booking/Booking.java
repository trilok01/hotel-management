package com.rupeek.hotelbooking.domain.booking;

import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.exception.InvalidBookingStateException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Booking {

    private final String id;
    private final String propertyId;
    private final String roomTypeId;
    private final String guestId;
    private final DateRange stayDates;
    private final int numGuests;
    private final int numRooms;
    private final Money totalAmount;
    private final LocalDateTime createdAt;
    private BookingStatus status;
    private LocalDateTime cancelledAt;
    private Money refundAmount;

    public Booking(String id, String propertyId, String roomTypeId, String guestId,
                    DateRange stayDates, int numGuests, int numRooms, Money totalAmount) {
        this.id = Objects.requireNonNull(id);
        this.propertyId = Objects.requireNonNull(propertyId);
        this.roomTypeId = Objects.requireNonNull(roomTypeId);
        this.guestId = Objects.requireNonNull(guestId);
        this.stayDates = Objects.requireNonNull(stayDates);
        if (numGuests <= 0) throw new IllegalArgumentException("numGuests must be positive");
        if (numRooms <= 0) throw new IllegalArgumentException("numRooms must be positive");
        this.numGuests = numGuests;
        this.numRooms = numRooms;
        this.totalAmount = Objects.requireNonNull(totalAmount);
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.CREATED;
    }

    /** Enforces the state machine defined in {@link BookingStatus}. */
    public void transitionTo(BookingStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidBookingStateException(
                    "Cannot transition booking " + id + " from " + status + " to " + target);
        }
        this.status = target;
    }

    public void markCancelled(Money refundAmount) {
        transitionTo(BookingStatus.CANCELLED);
        this.cancelledAt = LocalDateTime.now();
        this.refundAmount = refundAmount;
    }

    public String getId() {
        return id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public String getGuestId() {
        return guestId;
    }

    public DateRange getStayDates() {
        return stayDates;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public Money getRefundAmount() {
        return refundAmount;
    }
}
