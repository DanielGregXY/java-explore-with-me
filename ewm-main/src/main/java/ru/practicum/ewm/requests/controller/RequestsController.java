package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.RequestDTO;
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
    public List<RequestDTO> findParticipationRequests(@Positive @PathVariable Long userId) {
        return requestsService.findByRequestId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDTO createRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) {
        return requestsService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDTO cancelRequest(@PathVariable @Positive @NotNull Long userId,
                                    @PathVariable @Positive @NotNull Long requestId) {
        return requestsService.cancelRequest(userId, requestId);
    }
}
