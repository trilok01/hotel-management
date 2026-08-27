package com.rupeek.hotelbooking.domain.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class DateRange {

    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public DateRange(LocalDate checkIn, LocalDate checkOut) {
        Objects.requireNonNull(checkIn, "checkIn must not be null");
        Objects.requireNonNull(checkOut, "checkOut must not be null");
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("checkOut must be strictly after checkIn");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long nights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public boolean overlaps(DateRange other) {
        return this.checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(this.checkOut);
    }

    /** All calendar dates occupied by this range, i.e. [checkIn, checkOut). */
    public Iterable<LocalDate> nightsStream() {
        return () -> checkIn.datesUntil(checkOut).iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange)) return false;
        DateRange dateRange = (DateRange) o;
        return checkIn.equals(dateRange.checkIn) && checkOut.equals(dateRange.checkOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut);
    }

    @Override
    public String toString() {
        return checkIn + " to " + checkOut;
    }
}
