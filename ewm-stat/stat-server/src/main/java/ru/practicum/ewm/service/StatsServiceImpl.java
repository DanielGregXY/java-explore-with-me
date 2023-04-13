package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.dto.EndpointHitDTO;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDTO create(EndpointHitDTO endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(StatsMapper.STATS_MAPPER.toEndpointHit(endpointHitDto));
        log.info("Hit created with id {}", endpointHit.getId());
        return StatsMapper.STATS_MAPPER.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       Boolean unique) {
        log.info("Stats sent");
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsWithoutUriUnique(start, end).stream()
                        .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                return statsRepository.getStatsWithoutUriNotUnique(start, end).stream()
                        .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        } else if (unique) {
            return statsRepository.getStatsUnique(start, end, uris).stream()
                    .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            return statsRepository.getStatsNotUnique(start, end, uris).stream()
                    .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }
}
