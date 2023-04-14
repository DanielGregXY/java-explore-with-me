package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.CreateEventDTO;
import ru.practicum.ewm.events.dto.FullEventDTO;
import ru.practicum.ewm.events.dto.ShortEventDTO;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;

@Mapper
public interface EventMapper {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(category)")
    @Mapping(target = "eventState", constant = "PENDING")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", expression = "java(java.time.LocalDateTime.now())")
    Event toEventFromCreateDTO(User initiator, Category category, CreateEventDTO createEventDTO);

    @Mapping(target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "views", expression = "java(0L)")
    @Mapping(target = "state", expression = "java(event.getEventState().name())")
    FullEventDTO toFullEventDTO(Event event);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ShortEventDTO toShortEventDTO(Event event);

    ShortEventDTO toShortFromFull(FullEventDTO fullEventDTO);
}

