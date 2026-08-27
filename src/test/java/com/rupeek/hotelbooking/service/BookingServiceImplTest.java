package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.owner.Owner;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.exception.RoomUnavailableException;
import com.rupeek.hotelbooking.exception.ValidationException;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.OwnerRepository;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryBookingRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryOwnerRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryPropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplTest {

    private PropertyRepository propertyRepository;
    private BookingRepository bookingRepository;
    private RoomInventoryManager inventoryManager;
    private BookingService bookingService;
    private RoomType roomType;
    private Property property;

    @BeforeEach
    void setUp() {
        OwnerRepository ownerRepository = new InMemoryOwnerRepository();
        propertyRepository = new InMemoryPropertyRepository();
        bookingRepository = new InMemoryBookingRepository();
        inventoryManager = new RoomInventoryManager();
        bookingService = new BookingServiceImpl(propertyRepository, bookingRepository, inventoryManager);

        Owner owner = new Owner("owner-1", "Test Owner", "owner@test.com");
        ownerRepository.save(owner);

        property = new Property("prop-1", owner.getId(), "Test Hotel",
                new Location("Bengaluru", "Koramangala", "1 MG Road"), Set.of(), 4.0);
        roomType = new RoomType("rt-1", property.getId(), "Standard", Money.of(2000), 2, 2); // only 2 rooms total
        property.addRoomType(roomType);
        propertyRepository.save(property);
    }

    @Test
    void booksSuccessfullyWhenCapacityAvailable() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        var booking = bookingService.book(property.getId(), roomType.getId(), "guest-1", dates, 2, 1);

        assertNotNull(booking.getId());
        assertEquals(Money.of(2000).multiply(2), booking.getTotalAmount()); // 2 nights * 1 room
    }

    @Test
    void rejectsBookingWhenGuestsExceedRoomCapacity() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        assertThrows(ValidationException.class,
                () -> bookingService.book(property.getId(), roomType.getId(), "guest-1", dates, 10, 1));
    }

    @Test
    void rejectsBookingWhenInventoryExhausted() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        bookingService.book(property.getId(), roomType.getId(), "guest-1", dates, 2, 2);

        assertThrows(RoomUnavailableException.class,
                () -> bookingService.book(property.getId(), roomType.getId(), "guest-2", dates, 1, 1));
    }

    @Test
    void allowsBookingAfterNonOverlappingRangeFrees() {
        DateRange first = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        DateRange nonOverlapping = new DateRange(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));

        bookingService.book(property.getId(), roomType.getId(), "guest-1", first, 2, 2);

        assertDoesNotThrow(() ->
                bookingService.book(property.getId(), roomType.getId(), "guest-2", nonOverlapping, 2, 2));
    }

    @Test
    void concurrentBookingsForLastRoomOnlyOneSucceeds() throws InterruptedException {
        DateRange dates = new DateRange(LocalDate.now().plusDays(10), LocalDate.now().plusDays(11));
        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            int idx = i;
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    bookingService.book(property.getId(), roomType.getId(), "guest-" + idx, dates, 1, 1);
                    successes.incrementAndGet();
                } catch (RoomUnavailableException e) {
                    failures.incrementAndGet();
                } catch (InterruptedException ignored) {
                }
            });
        }

        ready.await();
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);

        assertEquals(2, successes.get());
        assertEquals(8, failures.get());
    }
}
