package ru.practicum.ewm.statistic;

import ru.practicum.ewm.dto.EndpointHitDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class HitMapper {
    public static EndpointHitDTO toEndpointHit(String app, HttpServletRequest request) {
        return new EndpointHitDTO(null,
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }
}
