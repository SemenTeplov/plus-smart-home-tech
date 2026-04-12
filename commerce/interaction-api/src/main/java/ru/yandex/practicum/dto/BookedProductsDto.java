package ru.yandex.practicum.dto;

import lombok.Builder;

@Builder
public record BookedProductsDto(
        Double deliveryWeight,
        Double deliveryVolume,
        Boolean fragile
) {
}
