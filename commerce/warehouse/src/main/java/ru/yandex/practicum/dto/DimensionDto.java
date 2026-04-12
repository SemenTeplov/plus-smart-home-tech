package ru.yandex.practicum.dto;

import lombok.Builder;

@Builder
public record DimensionDto(
        Double width,
        Double height,
        Double depth
) {
}
