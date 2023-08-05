package com.devlog.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {

    Page<EventDto> findAll(final EventSearch eventSearch, final Pageable pageable);
}
