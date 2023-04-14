package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.statistic.HitMapper;
import ru.practicum.ewm.statistic.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicEventService {
    private final EventRepository eventRepository;
    private final RequestsRepository requestsRepository;
    private final RatingRepository ratingRepository;
    private final StatService statService;

    public List<ShortEventDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable,
                                          HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeStart != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (text == null) text = "";
        List<Event> events = eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                categories, paid, start, end, pageable);
        List<FullEventDto> fullEventDtoList = events.stream()
                .map(EventMapper.EVENT_MAPPER::toFullEventDto)
                .collect(Collectors.toList());
        fullEventDtoList.forEach(event -> event.setConfirmedRequests(requestsRepository
                .findByEventIdConfirmed(event.getId()).size()));
        if (onlyAvailable) {
            fullEventDtoList = fullEventDtoList.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        statService.createView(HitMapper.toEndpointHit("ewm-main-service", request));
        List<ShortEventDto> eventsShort = EventUtil.getViews(fullEventDtoList, statService).stream()
                .map(EventMapper.EVENT_MAPPER::toShortFromFull)
                .collect(Collectors.toList());
        if (sort != null && sort.equalsIgnoreCase("VIEWS")) {
            eventsShort.sort((e1, e2) -> e2.getViews().compareTo(e1.getViews()));
        }
        EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
        EventUtil.getRatingToFullEvents(fullEventDtoList, ratingRepository);
        log.info("Events sent.");
        return eventsShort;
    }

    public FullEventDto findById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found.");
        });
        FullEventDto fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(requestsRepository.findAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size());
        EventUtil.getRatingToFullEvents(Collections.singletonList(fullEventDto), ratingRepository);
        statService.createView(HitMapper.toEndpointHit("ewm-main-service", request));
        log.info("Event sent.");
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
