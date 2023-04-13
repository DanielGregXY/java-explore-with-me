package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventUpdateRequestDTO;
import ru.practicum.ewm.events.dto.FullEventDTO;
import ru.practicum.ewm.events.service.AdminEventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public List<FullEventDTO> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<EventState> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        return adminEventService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public FullEventDTO updateEvent(@PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDTO eventUpdateRequestDTO) {
        return adminEventService.updateEvent(eventId, eventUpdateRequestDTO);
    }
}
