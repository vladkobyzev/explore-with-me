package ru.practicum.services.compilations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.compilations.UpdateCompilationRequest;
import ru.practicum.dto.events.EventFullDto;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.services.events.EventService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final ModelMapper modelMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setEvents(eventService.getAllEventsIn(newCompilationDto.getEvents()));
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        return convertEntityToDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFound("Compilation with id=" + compId + " was not found"));
        compilation.setEvents(eventService.getAllEventsIn(updateCompilationRequest.getEvents()));
        compilation.setPinned(updateCompilationRequest.isPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());
        return convertEntityToDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAllCompilations(boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if(pinned) {
            compilations = compilationRepository.findAllByPinned(true);
        } else {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            compilations = compilationRepository.findAll(pageRequest).toList();
        }
        return compilations.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        return convertEntityToDto(compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFound("Compilation with id=" + compId + " was not found")));
    }


    private CompilationDto convertEntityToDto(Compilation compilation) {
        return modelMapper.map(compilation, CompilationDto.class);
    }

}
