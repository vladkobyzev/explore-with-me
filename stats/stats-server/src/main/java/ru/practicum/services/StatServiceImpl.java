package ru.practicum.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stat;
import ru.practicum.repositories.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class StatServiceImpl implements StatService{
    private final ModelMapper mapper;
    private final StatRepository repository;

    public StatServiceImpl(ModelMapper mapper, StatRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public HitDto saveStat(HitDto hitDto) {
        Stat stat = convertDtoToEntity(hitDto);
        stat.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return convertEntityToDto(repository.save(stat));
    }

    @Override
    public List<ViewStatsDto> getStat(String start, String end, String[] uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        List<String> urisList = uris == null ? Collections.emptyList() : Arrays.asList(uris);
        if (urisList.isEmpty() && unique) {
            return repository.findAllByIpUniqueNoUris(startDate, endDate);
        } else if (urisList.isEmpty()) {
            return repository.findAllByIpNoUris(startDate, endDate);
        } else if (unique) {
            return repository.findAllByIpUnique(startDate, endDate, urisList);
        } else {
            return repository.findAllByIp(startDate, endDate, urisList);
        }
    }

    private HitDto convertEntityToDto(Stat stat) {
        return mapper.map(stat, HitDto.class);
    }

    private Stat convertDtoToEntity(HitDto hitDto) {
        return mapper.map(hitDto, Stat.class);
    }
}
