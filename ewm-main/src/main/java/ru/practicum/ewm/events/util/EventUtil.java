package ru.practicum.ewm.events.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.rating.dto.RatingView;
import ru.practicum.ewm.rating.repository.RatingRepository;
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

    public static List<FullEventDTO> getViews(List<FullEventDTO> eventDTOs, StatService statService) {
        Map<String, FullEventDTO> views = eventDTOs.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));
        Object responseBody = statService.getViewStats(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDTOs = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDTOs.forEach(viewStatsDTO -> {
            if (views.containsKey(viewStatsDTO.getUri())) {
                views.get(viewStatsDTO.getUri()).setViews(viewStatsDTO.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static List<ShortEventDTO> getViewsToShort(List<ShortEventDTO> eventDTOs, StatService statService) {
        Map<String, ShortEventDTO> views = eventDTOs.stream()
                .collect(Collectors.toMap(fullEventDTO -> "/events/" + fullEventDTO.getId(),
                        fullEventDTO -> fullEventDTO));
        Object responseBody = statService.getViewStats(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        List<ViewStatsDto> viewStatsDTOs = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDTOs.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    public static void getConfirmedRequests(List<FullEventDTO> eventDTOs,
                                            RequestsRepository requestsRepository) {
        List<Long> ids = eventDTOs.stream()
                .map(FullEventDTO::getId)
                .collect(Collectors.toList());
        List<ParticipationRequest> requests = requestsRepository.findConfirmedToListEvents(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDTOs.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static void getConfirmedRequestsToShort(List<ShortEventDTO> eventDTOs,
                                                   RequestsRepository requestsRepository) {
        List<Long> ids = eventDTOs.stream()
                .map(ShortEventDTO::getId)
                .collect(Collectors.toList());
        List<ParticipationRequest> requests = requestsRepository.findConfirmedToListEvents(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDTOs.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }

    public static void getRatingToFullEvents(List<FullEventDTO> eventDTOs, RatingRepository ratingRepository) {
        List<Long> ids = eventDTOs.stream()
                .map(FullEventDTO::getId)
                .collect(Collectors.toList());
        List<RatingView> likes = ratingRepository.getLikes(ids);
        List<RatingView> dislikes = ratingRepository.getDislikes(ids);
        Map<Long, Long> counter = new HashMap<>();
        likes.forEach(element -> counter.put(element.getEventId(), element.getTotal()));
        dislikes.forEach(element -> {
            if (counter.containsKey(element.getEventId())) {
                counter.put(element.getEventId(), counter.get(element.getEventId()) - element.getTotal());
            } else {
                counter.put(element.getEventId(), -element.getTotal());
            }
        });
        eventDTOs.forEach(element -> element.setRating(counter.getOrDefault(element.getId(), 0L)));
    }

    public static void getRatingToShortEvents(List<ShortEventDTO> eventDTOs, RatingRepository ratingRepository) {
        List<Long> ids = eventDTOs.stream()
                .map(ShortEventDTO::getId)
                .collect(Collectors.toList());
        List<RatingView> likes = ratingRepository.getLikes(ids);
        List<RatingView> dislikes = ratingRepository.getDislikes(ids);
        Map<Long, Long> counter = new HashMap<>();
        likes.forEach(element -> counter.put(element.getEventId(), element.getTotal()));
        dislikes.forEach(element -> {
            if (counter.containsKey(element.getEventId())) {
                counter.put(element.getEventId(), counter.get(element.getEventId()) - element.getTotal());
            } else {
                counter.put(element.getEventId(), -element.getTotal());
            }
        });
        eventDTOs.forEach(element -> element.setRating(counter.getOrDefault(element.getId(), 0L)));
    }

    public static void toEventFromUpdateRequestDto(Event event,
                                                   EventUpdateRequestDTO eventUpdateRequestDTO) {
        if (Objects.equals(eventUpdateRequestDTO.getStateAction(), UserActionState.CANCEL_REVIEW.name())) {
            event.setEventState(EventState.CANCELED);
        }
        if (Objects.equals(eventUpdateRequestDTO.getStateAction(), UserActionState.SEND_TO_REVIEW.name())) {
            event.setEventState(EventState.PENDING);
        }
        if (eventUpdateRequestDTO.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDTO.getAnnotation());
        }
        if (eventUpdateRequestDTO.getDescription() != null) {
            event.setDescription(eventUpdateRequestDTO.getDescription());
        }
        if (eventUpdateRequestDTO.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUpdateRequestDTO.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (eventUpdateRequestDTO.getPaid() != null) {
            event.setPaid(eventUpdateRequestDTO.getPaid());
        }
        if (eventUpdateRequestDTO.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDTO.getParticipantLimit());
        }
        if (eventUpdateRequestDTO.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDTO.getRequestModeration());
        }
        if (eventUpdateRequestDTO.getTitle() != null) {
            event.setTitle(eventUpdateRequestDTO.getTitle());
        }
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static LocalDateTime toTime(String timeString) throws DateTimeParseException {
        return LocalDateTime.parse(timeString, FORMATTER);
    }
}
