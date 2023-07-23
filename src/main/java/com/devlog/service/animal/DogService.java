package com.devlog.service.animal;

import org.springframework.stereotype.Service;

import com.devlog.domain.AnimalType;

@Service
public class DogService implements AnimalService {

    @Override
    public String getSound() {
        return "멍멍";
    }

    @Override
    public AnimalType getType() {
        return AnimalType.DOG;
    }
}
