package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.AdminStateAction;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDTO;
import ru.practicum.ewm.events.dto.FullEventDTO;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.CriteriaEventRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.rating.repository.RatingRepository;
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
    private final RatingRepository ratingRepository;
    private final StatService statService;

    public List<FullEventDTO> findEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                         String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<FullEventDTO> fullEventDTOList = criteriaEventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toFullEventDTO)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequests(fullEventDTOList, requestsRepository);
        EventUtil.getRatingToFullEvents(fullEventDTOList, ratingRepository);
        return EventUtil.getViews(fullEventDTOList, statService);
    }

    @Transactional
    public FullEventDTO updateEvent(Long eventId, EventUpdateRequestDTO eventUpdateRequestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found.");
        });
        if (eventUpdateRequestDTO.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDTO.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Date in the past.");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDTO.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDTO.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is already published.");
        }
        if (event.getEventState() == EventState.CANCELED
                && eventUpdateRequestDTO.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is canceled.");
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDTO.getStateAction().equalsIgnoreCase(AdminStateAction.REJECT_EVENT.name())) {
            throw new ConflictException("Event is published. You can't reject it.");
        }
        if (eventUpdateRequestDTO.getStateAction() != null) {
            if (eventUpdateRequestDTO.getStateAction().equals(AdminStateAction.PUBLISH_EVENT.name())) {
                event.setEventState(EventState.PUBLISHED);
            } else if (eventUpdateRequestDTO.getStateAction().equals(AdminStateAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                event.setEventState(EventState.CANCELED);
            }
        }
        if (eventUpdateRequestDTO.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDTO.getCategory()).orElseThrow(() -> {
                throw new ObjectNotFoundException("Category not found for update.");
            });
            event.setCategory(category);
        }
        if (eventUpdateRequestDTO.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdateRequestDTO.getLocation()));
        }
        EventUtil.toEventFromUpdateRequestDto(event, eventUpdateRequestDTO);
        eventRepository.save(event);
        FullEventDTO fullEventDTO = EventMapper.EVENT_MAPPER.toFullEventDTO(event);
        EventUtil.getConfirmedRequests(Collections.singletonList(fullEventDTO), requestsRepository);
        EventUtil.getRatingToFullEvents(Collections.singletonList(fullEventDTO), ratingRepository);
        return EventUtil.getViews(Collections.singletonList(fullEventDTO), statService).get(0);
    }
}
