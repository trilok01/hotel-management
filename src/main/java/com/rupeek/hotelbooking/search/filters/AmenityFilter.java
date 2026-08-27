package com.rupeek.hotelbooking.search.filters;

import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.search.PropertyFilter;
import com.rupeek.hotelbooking.search.SearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class AmenityFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.getRequiredAmenities() == null || criteria.getRequiredAmenities().isEmpty()) {
            return true;
        }
        return property.getAmenities().containsAll(criteria.getRequiredAmenities());
    }
}
