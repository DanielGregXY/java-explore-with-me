package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequestId(Long userId);

    Optional<ParticipationRequest> findByIdAndRequestId(Long requestId, Long userId);

    @Query("select participationRequest from ParticipationRequest participationRequest " +
            "where participationRequest.event.id = :eventId " +
            "and participationRequest.requester.id = :userId")
    Optional<ParticipationRequest> findByEventIdAndRequestId(@Param("eventId") Long eventId,
                                                             @Param("userId") Long userId);

    @Query("select participationRequest from ParticipationRequest participationRequest " +
            "where participationRequest.event.id = :eventId " +
            "and participationRequest.event.initiator.id = :userId")
    List<ParticipationRequest> findByEventIdAndInitiatorId(@Param("eventId") Long eventId,
                                                           @Param("userId") Long userId);

    @Query("select p from ParticipationRequest p " +
            "where p.event.id = :eventId and p.status = 'CONFIRMED'")
    List<ParticipationRequest> findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query("select p from ParticipationRequest p " +
            "where p.status = 'CONFIRMED' " +
            "and p.event.id IN (:events)")
    List<ParticipationRequest> findConfirmedToListEvents(@Param("events") List<Long> events);

    @Query("select participationRequest from ParticipationRequest participationRequest " +
            "where participationRequest.event.id = :event " +
            "and participationRequest.id IN (:requestIds)")
    List<ParticipationRequest> findByEventIdAndRequestIds(@Param("event") Long eventId,
                                                          @Param("requestIds") List<Long> requestIds);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
