package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.service.PrivateEventService;
import ru.practicum.ewm.requests.dto.RequestDTO;
import ru.practicum.ewm.requests.dto.RequestUpdateDTO;
import ru.practicum.ewm.requests.service.RequestsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService privateEventService;
    private final RequestsService requestsService;

    @GetMapping
    public List<ShortEventDTO> getEventsByCreator(@Positive @PathVariable Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return privateEventService.getEventsByCreator(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDTO create(@Positive @PathVariable Long userId,
                               @Valid @RequestBody CreateEventDTO createEventDTO) {
        return privateEventService.create(userId, createEventDTO);
    }

    @GetMapping("/{eventId}")
    public FullEventDTO getEventInfoByCreator(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return privateEventService.getEventInfoByCreator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDTO updateEvent(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDTO eventUpdateRequestDTO) {
        return privateEventService.updateEventByCreator(userId, eventId, eventUpdateRequestDTO);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDTO> findEventRequests(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return requestsService.findByEventIdAndInitiatorId(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateDTO requestProcessing(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId,
                                              @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestsService.requestProcessing(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
