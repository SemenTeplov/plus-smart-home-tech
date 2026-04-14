package ru.yandex.practicum.dto;

import lombok.Builder;

@Builder
public record SortObject(
        String direction,
        String nullHandling,
        Boolean ascending,
        String property,
        Boolean ignoreCase
) {
}
