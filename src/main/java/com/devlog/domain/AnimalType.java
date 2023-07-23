package com.devlog.domain;

public enum AnimalType {
    CAT,
    DOG;

    public boolean matchesName(final String type) {
        return this.name().equals(type);
    }
}
