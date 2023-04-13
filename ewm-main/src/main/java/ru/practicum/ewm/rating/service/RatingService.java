package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.rating.dto.RatingDTO;
import ru.practicum.ewm.rating.mapper.RatingMapper;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final EventRepository eventRepository;
    private final RatingRepository ratingRepository;

    public RatingDTO create(Long userId, RatingDTO ratingDTO) {
        Event event = eventRepository.findById(ratingDTO.getEventId()).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found.");
        });
        if (!event.getEventState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("This event not published yet.");
        }
        Rating rating = ratingRepository.save(RatingMapper.RATING_MAPPER.toRating(ratingDTO, userId, event));
        return RatingMapper.RATING_MAPPER.toRatingDTO(rating);
    }

    public void delete(Long userId, Long ratingId) {
        ratingRepository.findByIdAndUserId(ratingId, userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Your vote not found.");
        });
        ratingRepository.deleteById(ratingId);
    }
}
