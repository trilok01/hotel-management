package com.rupeek.hotelbooking.search.filters;

import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.search.PropertyFilter;
import com.rupeek.hotelbooking.search.SearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class CityFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.getCity() == null || criteria.getCity().isBlank()) {
            return true;
        }
        return property.getLocation().getCity().equalsIgnoreCase(criteria.getCity());
    }
}
