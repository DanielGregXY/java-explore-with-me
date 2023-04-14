package ru.practicum.ewm.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.requests.dto.RequestDTO;
import ru.practicum.ewm.requests.model.ParticipationRequest;

@Mapper
public interface RequestsMapper {
    RequestsMapper REQUESTS_MAPPER = Mappers.getMapper(RequestsMapper.class);

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDTO toRequestDTO(ParticipationRequest participationRequest);
}
