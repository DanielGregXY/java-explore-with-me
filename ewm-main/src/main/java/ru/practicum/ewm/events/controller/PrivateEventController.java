package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.service.PrivateEventService;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
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
    public List<ShortEventDto> getEventsByCreator(@Positive @PathVariable Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return privateEventService.getEventsByCreator(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto create(@Positive @PathVariable Long userId,
                               @Valid @RequestBody CreateEventDto createEventDto) {
        return privateEventService.create(userId, createEventDto);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getEventInfoByCreator(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return privateEventService.getEventInfoByCreator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return privateEventService.updateEventByCreator(userId, eventId, eventUpdateRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> findEventRequests(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return requestsService.findByEventIdAndInitiatorId(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateDto requestProcessing(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId,
                                              @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestsService.requestProcessing(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
