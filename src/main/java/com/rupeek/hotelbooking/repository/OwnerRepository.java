package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.owner.Owner;

import java.util.Optional;

public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(String id);
    boolean existsById(String id);
}
