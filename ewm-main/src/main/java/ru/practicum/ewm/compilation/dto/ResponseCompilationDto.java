package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.events.dto.ShortEventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCompilationDto {
    private Long id;
    private List<ShortEventDto> events;
    private Boolean pinned;
    private String title;
}
