package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query(value = "SELECT s.app, s.uri, count(DISTINCT s.ip) as hits " +
            "FROM statistics AS s " +
            "WHERE s.view_timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.uri, s.app " +
            "ORDER BY hits DESC ", nativeQuery = true)
    List<ViewStatsDto> findAllByIpUniqueNoUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT s.app, s.uri, count(s.ip) as hits " +
            "FROM statistics AS s " +
            "WHERE s.view_timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.uri, s.app " +
            "ORDER BY hits DESC ", nativeQuery = true)
    List<ViewStatsDto> findAllByIpNoUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT s.app, s.uri, count(DISTINCT s.ip) as hits " +
            "FROM statistics AS s " +
            "WHERE s.view_timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN ?3 " +
            "GROUP BY s.uri, s.app " +
            "ORDER BY hits DESC ", nativeQuery = true)
    List<ViewStatsDto> findAllByIpUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT s.app, s.uri, count(s.ip) as hits " +
            "FROM statistics AS s " +
            "WHERE s.view_timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN ?3 " +
            "GROUP BY s.uri, s.app " +
            "ORDER BY hits DESC ", nativeQuery = true)
    List<ViewStatsDto> findAllByIp(LocalDateTime start, LocalDateTime end, List<String> uris);

}
