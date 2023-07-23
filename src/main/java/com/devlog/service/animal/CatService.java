package com.devlog.service.animal;

import org.springframework.stereotype.Service;

import com.devlog.domain.AnimalType;

@Service
public class CatService implements AnimalService {

    @Override
    public String getSound() {
        return "야옹";
    }

    @Override
    public AnimalType getType() {
        return AnimalType.CAT;
    }
}
