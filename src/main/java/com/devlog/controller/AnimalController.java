package com.devlog.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.service.animal.AnimalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnimalController {

    // 1. 리스트 주입
    // private List<AnimalService> animalServices;

    // 2. 리스트 주입 (in class)
    // private final AnimalServiceFinder animalServiceFinder;

    /**
     * 3. map (key:beanName, value:service)
     * <bean_name, object_ref>
     */
    private final Map<String, AnimalService> animalServiceFinder;

    @GetMapping("/sound")
    public String sound(@RequestParam String type) {
        // return animalServiceFinder.findByType(type).getSound();
        return animalServiceFinder.get(type.toLowerCase() + "Service").getSound();
    }
}
