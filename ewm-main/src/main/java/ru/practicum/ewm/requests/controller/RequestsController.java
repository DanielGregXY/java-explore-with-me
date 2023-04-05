package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.service.RequestsService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

    @GetMapping
    public List<RequestDto> findParticipationRequests(@Positive @PathVariable Long userId) {
        return requestsService.findByRequestorId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) {
        return requestsService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive @NotNull Long userId,
                                    @PathVariable @Positive @NotNull Long requestId) {
        return requestsService.cancelRequest(userId, requestId);
    }
}
