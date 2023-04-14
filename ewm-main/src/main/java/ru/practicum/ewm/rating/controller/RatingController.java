package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rating/{userId}")
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto create(@Positive @PathVariable Long userId,
                            @Valid @RequestBody RatingDto ratingDTO) {
        return ratingService.create(userId, ratingDTO);
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long userId,
                       @Positive @PathVariable Long ratingId) {
        ratingService.delete(userId, ratingId);
    }

}
