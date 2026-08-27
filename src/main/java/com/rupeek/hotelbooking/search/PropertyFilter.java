package com.rupeek.hotelbooking.search;

import com.rupeek.hotelbooking.domain.property.Property;

public interface PropertyFilter {
    boolean matches(Property property, SearchCriteria criteria);
}
