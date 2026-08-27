package com.rupeek.hotelbooking.controller;

import com.rupeek.hotelbooking.domain.owner.Owner;
import com.rupeek.hotelbooking.dto.OwnerRequest;
import com.rupeek.hotelbooking.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    public ResponseEntity<Owner> onboard(@Valid @RequestBody OwnerRequest request) {
        Owner owner = ownerService.onboardOwner(request.name(), request.contactEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(owner);
    }

    @GetMapping("/{ownerId}")
    public Owner get(@PathVariable String ownerId) {
        return ownerService.getOwner(ownerId);
    }
}
