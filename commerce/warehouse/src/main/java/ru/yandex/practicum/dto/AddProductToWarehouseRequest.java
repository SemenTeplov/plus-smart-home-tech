package ru.yandex.practicum.dto;

import java.util.UUID;

public record AddProductToWarehouseRequest(
        UUID productId,
        Long quantity
) {
}
