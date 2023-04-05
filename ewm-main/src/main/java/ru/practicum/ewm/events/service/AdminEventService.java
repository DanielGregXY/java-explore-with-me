package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.AdminStateAction;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDto;
import ru.practicum.ewm.events.dto.FullEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.CriteriaEventRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.statistic.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {
    private final CriteriaEventRepository criteriaEventRepository;
    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestsRepository requestsRepository;
    private final StatService statService;

    public List<FullEventDto> findEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                         String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<FullEventDto> fullEventDtoList = criteriaEventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toFullEventDto)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
        return EventUtil.getViews(fullEventDtoList, statService);
    }

    @Transactional
    public FullEventDto updateEvent(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found");
        });
        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Date in the past");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is already published");
        }
        if (event.getEventState() == EventState.CANCELED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is canceled");
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.REJECT_EVENT.name())) {
            throw new ConflictException("Event is published. You can't reject it");
        }
        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT.name())) {
                event.setEventState(EventState.PUBLISHED);
            } else if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                event.setEventState(EventState.CANCELED);
            }
        }
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow(() -> {
                throw new ObjectNotFoundException("Category not found for update");
            });
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdateRequestDto.getLocation()));
        }
        EventUtil.toEventFromUpdateRequestDto(event, eventUpdateRequestDto);
        eventRepository.save(event);
        FullEventDto fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDto(event);
        EventUtil.getConfirmedRequests(Collections.singletonList(fullEventDto), requestsRepository);
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
