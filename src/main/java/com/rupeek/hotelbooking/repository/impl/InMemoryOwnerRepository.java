package com.rupeek.hotelbooking.repository.impl;

import com.rupeek.hotelbooking.domain.owner.Owner;
import com.rupeek.hotelbooking.repository.OwnerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOwnerRepository implements OwnerRepository {

    private final Map<String, Owner> store = new ConcurrentHashMap<>();

    @Override
    public Owner save(Owner owner) {
        store.put(owner.getId(), owner);
        return owner;
    }

    @Override
    public Optional<Owner> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
