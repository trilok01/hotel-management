package com.rupeek.hotelbooking.search;

import java.util.Set;

import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Amenity;

public class SearchCriteria {

    private final String city;
    private final DateRange dates;
    private final int numGuests;
    private final Money minPrice;
    private final Money maxPrice;
    private final Set<Amenity> requiredAmenities;
    private final Double minStarRating;

    private SearchCriteria(Builder b) {
        this.city = b.city;
        this.dates = b.dates;
        this.numGuests = b.numGuests;
        this.minPrice = b.minPrice;
        this.maxPrice = b.maxPrice;
        this.requiredAmenities = b.requiredAmenities;
        this.minStarRating = b.minStarRating;
    }

public static Builder builder() {
        return new Builder();
    }

    public String getCity() {
        return city;
    }

    public DateRange getDates() {
        return dates;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public Money getMinPrice() {
        return minPrice;
    }

    public Money getMaxPrice() {
        return maxPrice;
    }

    public Set<Amenity> getRequiredAmenities() {
        return requiredAmenities;
    }

    public Double getMinStarRating() {
        return minStarRating;
    }

    public static class Builder {
        private String city;
        private DateRange dates;
        private int numGuests = 1;
        private Money minPrice;
        private Money maxPrice;
        private Set<Amenity> requiredAmenities = Set.of();
        private Double minStarRating;

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder dates(DateRange dates) {
            this.dates = dates;
            return this;
        }

        public Builder numGuests(int numGuests) {
            this.numGuests = numGuests;
            return this;
        }

        public Builder priceRange(Money minPrice, Money maxPrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder requiredAmenities(Set<Amenity> amenities) {
            this.requiredAmenities = amenities == null ? Set.of() : amenities;
            return this;
        }

        public Builder minStarRating(Double minStarRating) {
            this.minStarRating = minStarRating;
            return this;
        }

        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
}
