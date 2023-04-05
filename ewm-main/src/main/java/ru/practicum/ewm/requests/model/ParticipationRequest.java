package ru.practicum.ewm.requests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    @Enumerated(EnumType.STRING)
    RequestStatus status;
}
