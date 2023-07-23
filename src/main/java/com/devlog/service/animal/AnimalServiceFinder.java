package com.devlog.service.animal;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnimalServiceFinder {

    private final List<AnimalService> animalServices;

    public AnimalService findByType(final String type) {
        return animalServices.stream()
            .filter(animalService -> matchType(type, animalService))
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }

    private boolean matchType(final String type, final AnimalService animalService) {
        return animalService.getType().matchesName(type);
    }
}
