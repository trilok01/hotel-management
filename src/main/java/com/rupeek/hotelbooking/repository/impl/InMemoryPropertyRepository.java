package com.rupeek.hotelbooking.repository.impl;

import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryPropertyRepository implements PropertyRepository {

    private final Map<String, Property> store = new ConcurrentHashMap<>();

    @Override
    public Property save(Property property) {
        store.put(property.getId(), property);
        return property;
    }

    @Override
    public Optional<Property> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Property> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Property> findByOwnerId(String ownerId) {
        return store.values().stream()
                .filter(p -> p.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
