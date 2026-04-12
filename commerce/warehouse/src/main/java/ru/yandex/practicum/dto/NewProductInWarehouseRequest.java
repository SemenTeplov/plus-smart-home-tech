package ru.yandex.practicum.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record NewProductInWarehouseRequest(
        UUID productId,
        Boolean fragile,
        DimensionDto dimension,
        Double weight
) {
}
