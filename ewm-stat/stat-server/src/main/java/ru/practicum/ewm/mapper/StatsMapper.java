package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.dto.EndpointHitDTO;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

@Mapper
public interface StatsMapper {

    StatsMapper STATS_MAPPER = Mappers.getMapper(StatsMapper.class);

    EndpointHitDTO toEndpointHitDto(EndpointHit endpointHit);

    EndpointHit toEndpointHit(EndpointHitDTO endpointHitDto);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}
