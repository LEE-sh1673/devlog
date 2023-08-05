package com.devlog.blog;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    @GetMapping("/events")
    public Page<EventDto> events(
        @RequestBody(required = false) EventSearch eventSearch,
        @PageableDefault(size = 5) Pageable pageable) {

        if (eventSearch == null) {
            return eventRepository.findAll(pageable)
                .map(event -> mapper.map(event, EventDto.class));
        }
        return eventRepository.findAll(eventSearch, pageable);
    }
}
