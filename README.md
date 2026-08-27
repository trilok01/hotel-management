# Hotel Booking System — Machine Coding Submission

A Spring Boot backend for a hotel booking platform: property discovery, onboarding, booking, payment,
and cancellation, with a REST API on top of an in-memory persistence layer.

## How to run

Requires **Java 17+** and **Maven**.

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080` and seeds two sample owners on boot (see console output
for their IDs): an independent host with a single property, and a chain with two properties — to
show the "single property" and "multi-property owner" cases are the same model.

To run the tests:

```bash
mvn test
```

## API quick tour

```bash
# Onboard an owner
curl -X POST localhost:8080/api/owners -H 'Content-Type: application/json' \
  -d '{"name":"Rina'\''s Homestay","contactEmail":"rina@example.com"}'

# Add a property under that owner
curl -X POST localhost:8080/api/properties -H 'Content-Type: application/json' \
  -d '{"ownerId":"<ownerId>","name":"Lake View Cottage","city":"Udaipur","locality":"Fatehsagar","amenities":["WIFI","PARKING"],"starRating":4.2}'

# Add a room type
curl -X POST localhost:8080/api/properties/<propertyId>/room-types -H 'Content-Type: application/json' \
  -d '{"name":"Standard Room","pricePerNight":2500,"maxGuestsPerRoom":2,"totalRooms":5}'

# Search
curl 'localhost:8080/api/search?city=Udaipur&checkIn=2026-09-01&checkOut=2026-09-03&guests=2'

# Book
curl -X POST localhost:8080/api/bookings -H 'Content-Type: application/json' \
  -d '{"propertyId":"<propertyId>","roomTypeId":"<roomTypeId>","guestId":"guest-1","checkIn":"2026-09-01","checkOut":"2026-09-03","numGuests":2,"numRooms":1}'

# Pay
curl -X POST localhost:8080/api/bookings/<bookingId>/payments -H 'Content-Type: application/json' \
  -d '{"method":"CARD"}'

# Cancel
curl -X POST localhost:8080/api/bookings/<bookingId>/cancel
```

## Design decisions

**Owner is the single abstraction for hosts and chains.** There's no `SingleProperty` vs
`ChainProperty` type. An `Owner` just has zero-or-more `Property` records pointing at it. A host with
one property and a hotel chain with fifty are the same code path — adding a second property to an
"independent" owner requires no migration, no type change, nothing.

**RoomType, not Property, is the unit of bookable inventory.** A property lists its room types
(Standard, Suite, ...), each with its own room count and nightly rate. Availability, booking, and
concurrency are all scoped to a room type + date range, which is what actually needs to be checked
for a double-booking.

**Search is filter-composition, not a big conditional.** `PropertySearchService` is injected with
the full list of `PropertyFilter` Spring beans (`CityFilter`, `PriceRangeFilter`, `AmenityFilter`,
`StarRatingFilter`, `AvailabilityFilter`) and ANDs them together. A new filter is a new
`@Component` — the search service itself never changes. `Amenity` is an open value object (not an
enum) for the same reason: new amenities are just data.

**Concurrency-safe inventory via per-room-type locks.** `RoomInventoryManager` holds a
`ReentrantLock` per room type and does "check every night in the range has capacity, then commit"
as one atomic critical section. Two threads racing for the last room on the same dates cannot both
win — this is covered by a dedicated concurrent unit test (10 threads racing for 2 rooms; exactly 2
succeed). Different room types never block each other. Date ranges are half-open
(`[checkIn, checkOut)`), so a checkout on day N and a check-in on day N for the same room correctly
don't conflict.

**Booking has an explicit, enforced state machine.**
`CREATED → PAYMENT_PENDING → CONFIRMED → COMPLETED`, with `PAYMENT_FAILED` as a retryable side
branch and `CANCELLED` reachable from every non-terminal state. Illegal transitions
(e.g. paying for an already-cancelled booking) throw from inside `Booking` itself, not from service
logic that could be bypassed.

**Payment methods are a pluggable strategy, not a switch statement.** `PaymentGateway` is an
interface; `CardPaymentGateway`, `UpiPaymentGateway`, `WalletPaymentGateway` are separate mocked
implementations, and `PaymentGatewayRegistry` resolves the right one by method type using Spring's
list-injection. Adding NetBanking means adding one new `@Component`. Payment outcome drives booking
state directly: success confirms, failure moves to `PAYMENT_FAILED` (inventory stays held so the
guest can retry rather than losing the room).

**Cancellation policy is pluggable and separate from the cancellation flow.**
`CancellationService` doesn't know the refund math; it asks a `CancellationPolicy` strategy.
`DefaultCancellationPolicy` implements a simple tiered rule (full refund 48h+ out, 50% between
24–48h, none inside 24h) purely as an example — swapping in a different policy (e.g. per-property,
non-refundable rate) doesn't touch the service.

**Repositories are interfaces over in-memory maps.** `OwnerRepository`, `PropertyRepository`,
`BookingRepository`, `PaymentRepository` are all interfaces; the only implementations provided are
`ConcurrentHashMap`-backed, but a JPA-backed implementation could be dropped in without touching any
service.

**Money and DateRange are immutable value objects**, not primitives — `Money` avoids floating-point
error and enforces non-negativity; `DateRange` enforces `checkOut > checkIn` and centralizes overlap
logic in one place.

## What's covered

- Search/discovery with pluggable filters (city, dates/availability, guests, price range,
  amenities, star rating).
- Onboarding a property under an owner, modeling single vs. multi-property owners identically.
- Booking with atomic availability check + reservation, guest-count validation against room
  capacity, and prevention of double-booking under concurrency.
- Payment via a mocked, pluggable gateway abstraction (Card, UPI, Wallet), driving booking state.
- Cancellation with a pluggable refund policy and inventory release.
- REST endpoints for all of the above, with input validation and a global exception handler mapping
  domain exceptions to appropriate HTTP status codes (404 / 400 / 409 / 402).
- Unit tests: booking state machine, double-booking prevention (including a concurrent-threads
  test), payment success/failure, cancellation + inventory release, and search filter behavior.

## Assumptions

- Single currency (INR-flavored `Money`); no multi-currency conversion.
- A "room" within a room type is fungible — we track counts per night, not individually numbered
  physical rooms.
- No authentication/authorization (explicitly out of scope) — `guestId`/`ownerId` are passed as
  plain strings.
- Payment gateways are fully mocked; `WalletPaymentGateway` has an artificial balance-limit check
  purely to exercise the failure path end-to-end.
- Refund policy is a simple lead-time tiering; a real system would likely also consider the payment
  method's own refund mechanics.

## With more time

- Idempotency keys on the payment endpoint (a booking dropped/retried mid-charge could otherwise
  double-charge with a real gateway).
- A pluggable dynamic-pricing strategy on top of `RoomType.basePricePerNight` (seasonal/demand
  based).
- OpenAPI/Swagger documentation.
- Persisting the inventory ledger and bookings behind the same repository abstraction with an actual
  database (H2/Postgres), including optimistic locking as a second line of defense alongside the
  in-process lock.
- Pagination and sorting on the search endpoint.
- More edge-case tests: overlapping-but-not-identical date ranges, cancelling a `PAYMENT_FAILED`
  booking, multi-room bookings spanning the total inventory boundary exactly.
