package ru.practicum.ewm.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.rating.dto.RatingView;
import ru.practicum.ewm.rating.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByIdAndUserId(Long ratingId, Long userId);

    @Query("select new ru.practicum.ewm.rating.dto.RatingView(r.event.id, count(r.id)) " +
            "from Rating r " +
            "where r.ratingState = 'LIKE' and r.event.eventState = 'PUBLISHED' and r.event.id in (:eventId)" +
            "group by r.event.id")
    List<RatingView> getLikes(@Param("eventId") List<Long> eventId);

    @Query("select new ru.practicum.ewm.rating.dto.RatingView(r.event.id, count(r.id)) " +
            "from Rating r " +
            "where r.ratingState = 'DISLIKE' and r.event.eventState = 'PUBLISHED' and r.event.id in (:eventId)" +
            "group by r.event.id")
    List<RatingView> getDislikes(@Param("eventId") List<Long> eventId);
}
