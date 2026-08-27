package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.owner.Owner;

public interface OwnerService {
    Owner onboardOwner(String name, String contactEmail);
    Owner getOwner(String ownerId);
}
