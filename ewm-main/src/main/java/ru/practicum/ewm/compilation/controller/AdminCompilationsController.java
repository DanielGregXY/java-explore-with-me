package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDTO) {
        return compilationsService.create(newCompilationDTO);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationsService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public ResponseCompilationDto update(@PathVariable Long compId,
                                         @RequestBody NewCompilationDto newCompilationDTO) {
        return compilationsService.update(compId, newCompilationDTO);
    }
}
