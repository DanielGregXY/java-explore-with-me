package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.NewCompilationDTO;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDTO;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.dto.ShortEventDTO;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDTO newCompilationDTO, List<Event> events) {
        return new Compilation(null,
                events,
                newCompilationDTO.getPinned(),
                newCompilationDTO.getTitle());
    }

    public static ResponseCompilationDTO toResponseCompilationDto(Compilation compilation) {
        List<ShortEventDTO> shortEvents = compilation.getEvents().stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDTO)
                .collect(Collectors.toList());
        return new ResponseCompilationDTO(compilation.getId(),
                shortEvents,
                compilation.getPinned(),
                compilation.getTitle());
    }
}
