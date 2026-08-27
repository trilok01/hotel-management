package com.rupeek.hotelbooking.domain.property;

import java.util.Locale;
import java.util.Objects;

public final class Amenity {

    public static final Amenity WIFI = new Amenity("WIFI");
    public static final Amenity POOL = new Amenity("POOL");
    public static final Amenity PARKING = new Amenity("PARKING");
    public static final Amenity BREAKFAST = new Amenity("BREAKFAST");
    public static final Amenity AC = new Amenity("AC");
    public static final Amenity GYM = new Amenity("GYM");
    public static final Amenity SPA = new Amenity("SPA");

    private final String code;

    public Amenity(String code) {
        Objects.requireNonNull(code, "code must not be null");
        this.code = code.trim().toUpperCase(Locale.ROOT);
    }

    public static Amenity of(String code) {
        return new Amenity(code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amenity)) return false;
        return code.equals(((Amenity) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
