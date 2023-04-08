package ru.practicum.ewm.events.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.statistic.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class EventUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final LocalDateTime MAX_TIME = toTime("5000-01-01 00:00:00");
    public static final LocalDateTime MIN_TIME = toTime("2000-01-01 00:00:00");

    public static List<FullEventDto> getViews(List<FullEventDto> eventDtos, StatService statService) {
        Map<String, FullEventDto> views = eventDtos.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));
        Object responseBody = statService.getViewStats(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDtos.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static List<ShortEventDto> getViewsToShort(List<ShortEventDto> eventDtos, StatService statService) {
        Map<String, ShortEventDto> views = eventDtos.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));
        Object responseBody = statService.getViewStats(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDtos.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static void getConfirmedRequests(List<FullEventDto> eventDtos,
                                            RequestsRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(FullEventDto::getId)
                .collect(Collectors.toList());
        List<ParticipationRequest> requests = requestsRepository.findConfirmedToListEvents(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static void getConfirmedRequestsToShort(List<ShortEventDto> eventDtos,
                                                   RequestsRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(ShortEventDto::getId)
                .collect(Collectors.toList());
        List<ParticipationRequest> requests = requestsRepository.findConfirmedToListEvents(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static void toEventFromUpdateRequestDto(Event event,
                                                   EventUpdateRequestDto eventUpdateRequestDto) {
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.CANCEL_REVIEW.name())) {
            event.setEventState(EventState.CANCELED);
        }
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.SEND_TO_REVIEW.name())) {
            event.setEventState(EventState.PENDING);
        }
        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (eventUpdateRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateRequestDto.getPaid());
        }
        if (eventUpdateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDto.getParticipantLimit());
        }
        if (eventUpdateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDto.getRequestModeration());
        }
        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static LocalDateTime toTime(String timeString) throws DateTimeParseException {
        return LocalDateTime.parse(timeString, FORMATTER);
    }
}
