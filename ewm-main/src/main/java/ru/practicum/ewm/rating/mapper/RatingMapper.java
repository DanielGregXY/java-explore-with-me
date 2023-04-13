package ru.practicum.ewm.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.rating.dto.RatingDTO;
import ru.practicum.ewm.rating.model.Rating;

@Mapper
public interface RatingMapper {
    RatingMapper RATING_MAPPER = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "userId", expression = "java(userId)")
    @Mapping(target = "event", expression = "java(event)")
    @Mapping(target = "id", expression = "java(ratingDTO.getId())")
    Rating toRating(RatingDTO ratingDTO, Long userId, Event event);

    @Mapping(target = "eventId", source = "event.id")
    RatingDTO toRatingDTO(Rating rating);
}
