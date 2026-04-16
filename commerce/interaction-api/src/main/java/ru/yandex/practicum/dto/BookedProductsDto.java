package ru.yandex.practicum.dto;

public record BookedProductsDto(
        Double deliveryWeight,
        Double deliveryVolume,
        Boolean fragile
) {
}
