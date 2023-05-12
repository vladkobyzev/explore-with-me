package ru.practicum.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.HitDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(@Value("${ewm-stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveStat(HitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStat(String start, String end, String[] uris, Boolean unique) {
        StringBuilder pathBuilder = new StringBuilder("/stats");
        Map<String, Object> parameters = new HashMap<>();
        pathBuilder.append("?start={start}&end={end}");
        parameters.put("start", start);
        parameters.put("end", end);

        if (uris != null) {
            pathBuilder.append("&uris={uris}");
            String allUri = String.join(",", uris);
            parameters.put("uris", allUri);
        }
        if (unique != null) {
            pathBuilder.append("&unique={unique}");
            parameters.put("unique", unique);

        }
        String path = pathBuilder.toString();
        return get(path, null, parameters);
    }
}
