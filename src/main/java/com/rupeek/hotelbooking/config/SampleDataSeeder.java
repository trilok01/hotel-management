package com.rupeek.hotelbooking.config;

import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.owner.Owner;
import com.rupeek.hotelbooking.domain.property.Amenity;
import com.rupeek.hotelbooking.domain.property.Location;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.service.OwnerService;
import com.rupeek.hotelbooking.service.PropertyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("!test")
public class SampleDataSeeder implements CommandLineRunner {

    private final OwnerService ownerService;
    private final PropertyService propertyService;

    public SampleDataSeeder(OwnerService ownerService, PropertyService propertyService) {
        this.ownerService = ownerService;
        this.propertyService = propertyService;
    }

    @Override
    public void run(String... args) {
        // A single-property host.
        Owner independentHost = ownerService.onboardOwner("Rina's Homestay", "rina@example.com");
        Property lakeView = propertyService.addProperty(independentHost.getId(), "Lake View Cottage",
                new Location("Udaipur", "Fatehsagar", "12 Lake Road"),
                Set.of(Amenity.WIFI, Amenity.PARKING), 4.2);
        propertyService.addRoomType(lakeView.getId(), "Standard Room", Money.of(2500), 2, 5);
        propertyService.addRoomType(lakeView.getId(), "Lake Facing Suite", Money.of(4500), 3, 2);

        // A multi-property chain - same Owner abstraction, just more properties.
        Owner chain = ownerService.onboardOwner("Zenith Hotels", "contact@zenithhotels.example");
        Property zenithBlr = propertyService.addProperty(chain.getId(), "Zenith Bengaluru",
                new Location("Bengaluru", "Indiranagar", "100 100ft Road"),
                Set.of(Amenity.WIFI, Amenity.POOL, Amenity.GYM, Amenity.BREAKFAST), 4.6);
        propertyService.addRoomType(zenithBlr.getId(), "Deluxe Room", Money.of(6000), 2, 20);
        propertyService.addRoomType(zenithBlr.getId(), "Executive Suite", Money.of(11000), 4, 6);

        Property zenithMum = propertyService.addProperty(chain.getId(), "Zenith Mumbai",
                new Location("Mumbai", "Bandra", "45 Linking Road"),
                Set.of(Amenity.WIFI, Amenity.SPA, Amenity.AC), 4.4);
        propertyService.addRoomType(zenithMum.getId(), "Deluxe Room", Money.of(7500), 2, 15);

        System.out.println("Seeded owner (single property): " + independentHost.getId());
        System.out.println("Seeded owner (chain, 2 properties): " + chain.getId());
    }
}
