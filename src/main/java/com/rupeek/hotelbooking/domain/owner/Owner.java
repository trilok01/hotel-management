package com.rupeek.hotelbooking.domain.owner;

import java.util.Objects;

public class Owner {

    private final String id;
    private String name;
    private String contactEmail;

    public Owner(String id, String name, String contactEmail) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.contactEmail = contactEmail;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
