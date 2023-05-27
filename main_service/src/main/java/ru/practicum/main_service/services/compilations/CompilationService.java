package ru.practicum.main_service.services.compilations;

import ru.practicum.main_service.dto.compilations.CompilationDto;
import ru.practicum.main_service.dto.compilations.NewCompilationDto;
import ru.practicum.main_service.dto.compilations.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compId);

    List<CompilationDto> getAllCompilations(boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(long compId);
}
