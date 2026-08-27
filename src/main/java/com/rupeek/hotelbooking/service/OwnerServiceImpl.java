package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.owner.Owner;
import com.rupeek.hotelbooking.exception.NotFoundException;
import com.rupeek.hotelbooking.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner onboardOwner(String name, String contactEmail) {
        Owner owner = new Owner(UUID.randomUUID().toString(), name, contactEmail);
        return ownerRepository.save(owner);
    }

    @Override
    public Owner getOwner(String ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found: " + ownerId));
    }
}
