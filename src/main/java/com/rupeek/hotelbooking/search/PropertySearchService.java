package com.rupeek.hotelbooking.search;

import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertySearchService {

    private final PropertyRepository propertyRepository;
    private final List<PropertyFilter> filters;

    public PropertySearchService(PropertyRepository propertyRepository, List<PropertyFilter> filters) {
        this.propertyRepository = propertyRepository;
        this.filters = filters;
    }

    public List<Property> search(SearchCriteria criteria) {
        return propertyRepository.findAll().stream()
                .filter(property -> filters.stream().allMatch(f -> f.matches(property, criteria)))
                .toList();
    }
}
