package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDTO, List<Event> events) {
        return new Compilation(null,
                events,
                newCompilationDTO.getPinned(),
                newCompilationDTO.getTitle());
    }

    public static ResponseCompilationDto toResponseCompilationDto(Compilation compilation) {
        List<ShortEventDto> shortEvents = compilation.getEvents().stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDto)
                .collect(Collectors.toList());
        return new ResponseCompilationDto(compilation.getId(),
                shortEvents,
                compilation.getPinned(),
                compilation.getTitle());
    }
}
