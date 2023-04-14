package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.repository.RequestsRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.AdminUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestsService {
    private final EventRepository eventRepository;
    private final AdminUserRepository userRepository;
    private final RequestsRepository requestsRepository;

    public List<RequestDto> findByRequestId(Long userId) {
        log.info("Request sent");
        return requestsRepository.findByRequesterId(userId).stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toRequestDTO)
                .collect(Collectors.toList());
    }

    public List<RequestDto> findByEventIdAndInitiatorId(Long eventId, Long userId) {
        log.info("Requests sent");
        return requestsRepository.findByEventIdAndInitiatorId(eventId, userId).stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("User not found");
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found");
        });
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published. Request rejected.");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("You can't send request to your own event.");
        }
        int confirmedRequests = requestsRepository.findByEventIdConfirmed(eventId).size();
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Participant limit reached.");
        }
        RequestStatus status = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        ParticipationRequest request = new ParticipationRequest(null,
                LocalDateTime.now(),
                event,
                user,
                status);
        Optional<ParticipationRequest> check = requestsRepository
                .findByEventIdAndRequestId(eventId, userId);
        if (check.isPresent()) throw new ConflictException("You already have request to event.");
        request = requestsRepository.save(request);
        log.info("Request created.");
        return RequestsMapper.REQUESTS_MAPPER.toRequestDTO(request);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestsRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> {
                    throw new ObjectNotFoundException("Request not found.");
                }
        );
        request.setStatus(RequestStatus.CANCELED);
        log.info("Request canceled.");
        return RequestsMapper.REQUESTS_MAPPER.toRequestDTO(requestsRepository.save(request));
    }

    @Transactional
    public RequestUpdateDto requestProcessing(Long userId, Long eventId,
                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found.");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("You don't have event with id %s", eventId));
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation is not required.");
        }
        RequestUpdateDto requestUpdateDTO = new RequestUpdateDto(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestsRepository.findByEventIdConfirmed(eventId).size();
        List<ParticipationRequest> requests = requestsRepository.findByEventIdAndRequestIds(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());
        if (Objects.equals(eventRequestStatusUpdateRequest.getStatus(), RequestStatus.CONFIRMED.name())
                && confirmedRequests + requests.size() > event.getParticipantLimit()) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<RequestDto> requestDTO = requests.stream()
                    .map(RequestsMapper.REQUESTS_MAPPER::toRequestDTO)
                    .collect(Collectors.toList());
            requestUpdateDTO.setRejectedRequests(requestDTO);
            requestsRepository.saveAll(requests);
            throw new ConflictException("Requests limit exceeded.");
        }
        if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            requests.forEach(request -> {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new ConflictException("You can't reject confirmed request.");
                }
                request.setStatus(RequestStatus.REJECTED);
            });
            List<RequestDto> requestDTO = requests.stream()
                    .map(RequestsMapper.REQUESTS_MAPPER::toRequestDTO)
                    .collect(Collectors.toList());
            requestUpdateDTO.setRejectedRequests(requestDTO);
            requestsRepository.saveAll(requests);
        } else if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.CONFIRMED.name())
                && eventRequestStatusUpdateRequest.getRequestIds().size() <= event.getParticipantLimit() - confirmedRequests) {
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            List<RequestDto> requestDTO = requests.stream()
                    .map(RequestsMapper.REQUESTS_MAPPER::toRequestDTO)
                    .collect(Collectors.toList());
            requestUpdateDTO.setConfirmedRequests(requestDTO);
            requestsRepository.saveAll(requests);
        }
        return requestUpdateDTO;
    }
}
