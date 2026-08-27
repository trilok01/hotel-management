package com.rupeek.hotelbooking.inventory;

import com.rupeek.hotelbooking.domain.common.DateRange;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class RoomInventoryManager {

    private final Map<String, RoomTypeLedger> ledgers = new ConcurrentHashMap<>();

    private RoomTypeLedger ledgerFor(String roomTypeId) {
        return ledgers.computeIfAbsent(roomTypeId, id -> new RoomTypeLedger());
    }

    public boolean tryReserve(String roomTypeId, DateRange dates, int numRooms, int totalRooms) {
        RoomTypeLedger ledger = ledgerFor(roomTypeId);
        ledger.lock.lock();
        try {
            for (LocalDate date : dates.nightsStream()) {
                int booked = ledger.bookedPerNight.getOrDefault(date, 0);
                if (booked + numRooms > totalRooms) {
                    return false;
                }
            }
            for (LocalDate date : dates.nightsStream()) {
                ledger.bookedPerNight.merge(date, numRooms, Integer::sum);
            }
            return true;
        } finally {
            ledger.lock.unlock();
        }
    }

    public boolean isAvailable(String roomTypeId, DateRange dates, int numRooms, int totalRooms) {
        RoomTypeLedger ledger = ledgerFor(roomTypeId);
        ledger.lock.lock();
        try {
            for (LocalDate date : dates.nightsStream()) {
                int booked = ledger.bookedPerNight.getOrDefault(date, 0);
                if (booked + numRooms > totalRooms) {
                    return false;
                }
            }
            return true;
        } finally {
            ledger.lock.unlock();
        }
    }

    public void release(String roomTypeId, DateRange dates, int numRooms) {
        RoomTypeLedger ledger = ledgerFor(roomTypeId);
        ledger.lock.lock();
        try {
            for (LocalDate date : dates.nightsStream()) {
                ledger.bookedPerNight.merge(date, -numRooms, Integer::sum);
                if (ledger.bookedPerNight.get(date) <= 0) {
                    ledger.bookedPerNight.remove(date);
                }
            }
        } finally {
            ledger.lock.unlock();
        }
    }

    private static final class RoomTypeLedger {
        final ReentrantLock lock = new ReentrantLock();
        final Map<LocalDate, Integer> bookedPerNight = new TreeMap<>();
    }
}
