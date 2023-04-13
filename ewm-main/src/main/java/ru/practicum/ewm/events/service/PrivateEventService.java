package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.util.EventUtil;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.statistic.StatService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.AdminUserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventService {
    private final RequestsRepository requestsRepository;
    private final RatingRepository ratingRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final AdminUserRepository adminUserRepository;
    private final CategoryRepository categoryRepository;
    private final StatService statService;

    @Transactional
    public FullEventDTO create(Long userId, CreateEventDTO createEventDTO) {
        if (createEventDTO.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Wrong date.");
        }
        User initiator = adminUserRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("User not found.");
        });
        Category category = categoryRepository.findById(createEventDTO.getCategory()).orElseThrow(() -> {
            throw new ObjectNotFoundException("Category not found.");
        });
        createEventDTO.setLocation(locationRepository.save(createEventDTO.getLocation()));
        Event event = eventRepository.save(EventMapper.EVENT_MAPPER.toEventFromCreateDTO(initiator, category, createEventDTO));
        FullEventDTO fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDTO(event);
        fullEventDto.setConfirmedRequests(0);
        fullEventDto.setRating(0L);
        return fullEventDto;
    }

    public List<ShortEventDTO> getEventsByCreator(Long userId, PageRequest pageable) {
        List<ShortEventDTO> shortEventDTOS = eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDTO)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequestsToShort(shortEventDTOS, requestsRepository);
        EventUtil.getRatingToShortEvents(shortEventDTOS, ratingRepository);
        return EventUtil.getViewsToShort(shortEventDTOS, statService);
    }

    public FullEventDTO getEventInfoByCreator(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow();

        FullEventDTO fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDTO(event);
        fullEventDto.setConfirmedRequests(requestsRepository
                .findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size());
        EventUtil.getRatingToFullEvents(Collections.singletonList(fullEventDto), ratingRepository);
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }

    @Transactional
    public FullEventDTO updateEventByCreator(Long userId, Long eventId, EventUpdateRequestDTO eventUpdateRequestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can't update this event.");
        }
        if (eventUpdateRequestDTO.getEventDate() != null) {
            LocalDateTime time = LocalDateTime.parse(eventUpdateRequestDTO.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (LocalDateTime.now().isAfter(time.minusHours(2))) {
                throw new ConflictException("Event starts in less then 2 hours.");
            }
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You can't update published event.");
        }
        if (eventUpdateRequestDTO.getCategory() != null && !Objects.equals(eventUpdateRequestDTO.getCategory(),
                event.getCategory().getId())) {
            Category category = categoryRepository.findById(eventUpdateRequestDTO.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (eventUpdateRequestDTO.getLocation() != null) {
            Location location = locationRepository.save(eventUpdateRequestDTO.getLocation());
            event.setLocation(location);
        }
        EventUtil.toEventFromUpdateRequestDto(event, eventUpdateRequestDTO);
        FullEventDTO fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDTO(event);
        fullEventDto.setConfirmedRequests(requestsRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                .size());
        EventUtil.getRatingToFullEvents(Collections.singletonList(fullEventDto), ratingRepository);
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
