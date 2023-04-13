package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.ResponseCompilationDTO;
import ru.practicum.ewm.compilation.service.CompilationsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final CompilationsService compilationsService;


    @GetMapping
    public List<ResponseCompilationDTO> findAll(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return compilationsService.findAll(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public ResponseCompilationDTO findById(@PathVariable Long compId) {
        return compilationsService.findById(compId);
    }
}
