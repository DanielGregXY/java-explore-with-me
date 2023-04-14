package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.NewCompilationDTO;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDTO;
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
    public ResponseCompilationDTO create(NewCompilationDTO newCompilationDTO) {
        List<Event> events = eventRepository.findByIds(newCompilationDTO.getEvents());
        return CompilationMapper.toResponseCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDTO, events)));
    }

    @Transactional
    public void delete(Long compId) {
        compilationRepository.findById(compId).orElseThrow();
        compilationRepository.deleteById(compId);
        log.info("Compilation with id {} deleted", compId);
    }

    @Transactional
    public ResponseCompilationDTO update(Long compId, NewCompilationDTO newCompilationDTO) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (newCompilationDTO.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIds(newCompilationDTO.getEvents()));
        }
        if (newCompilationDTO.getPinned() != null) {
            compilation.setPinned(newCompilationDTO.getPinned());
        }
        if (newCompilationDTO.getTitle() != null) {
            compilation.setTitle(newCompilationDTO.getTitle());
        }
        compilationRepository.save(compilation);
        log.info("Compilation {} updated", compId);
        return CompilationMapper.toResponseCompilationDto(compilation);
    }

    public List<ResponseCompilationDTO> findAll(Boolean pinned, Pageable pageable) {
        log.info("Compilations sent");
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toResponseCompilationDto)
                .collect(Collectors.toList());
    }

    public ResponseCompilationDTO findById(Long compId) {
        log.info("Compilation sent");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return CompilationMapper.toResponseCompilationDto(compilation);
    }
}
