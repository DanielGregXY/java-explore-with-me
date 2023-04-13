package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.events.dto.ShortEventDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCompilationDTO {
    private Long id;
    private List<ShortEventDTO> events;
    private Boolean pinned;
    private String title;
}
