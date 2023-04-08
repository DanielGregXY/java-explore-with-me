package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ResponseCompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findByIds(newCompilationDto.getEvents());
        return CompilationMapper.toResponseCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Transactional
    public void delete(Long compId) {
        compilationRepository.findById(compId).orElseThrow();
        compilationRepository.deleteById(compId);
        log.info("Compilation with id {} deleted", compId);
    }

    @Transactional
    public ResponseCompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIds(newCompilationDto.getEvents()));
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        compilationRepository.save(compilation);
        log.info("Compilation {} updated", compId);
        return CompilationMapper.toResponseCompilationDto(compilation);
    }

    public List<ResponseCompilationDto> findAll(Boolean pinned, Pageable pageable) {
        log.info("Compilations sent");
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toResponseCompilationDto)
                .collect(Collectors.toList());
    }

    public ResponseCompilationDto findById(Long compId) {
        log.info("Compilation sent");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return CompilationMapper.toResponseCompilationDto(compilation);
    }
}
