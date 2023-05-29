package ru.practicum.stats_models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStats {

    private String app;

    private String uri;

    private Long hits;
}
