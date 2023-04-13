package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDTO;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDTO create(EndpointHitDTO endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris,
                                Boolean unique);
}
