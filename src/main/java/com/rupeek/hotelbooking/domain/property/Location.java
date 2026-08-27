package com.rupeek.hotelbooking.domain.property;

import java.util.Objects;

public final class Location {

    private final String city;
    private final String locality;
    private final String address;

    public Location(String city, String locality, String address) {
        this.city = Objects.requireNonNull(city, "city must not be null");
        this.locality = locality;
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return city.equalsIgnoreCase(location.city) && Objects.equals(locality, location.locality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city.toLowerCase(), locality);
    }
}
