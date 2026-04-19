package ru.yandex.practicum.dto;

import java.util.UUID;

public record ChangeProductQuantityRequest(
        UUID productId,
        Long newQuantity
) {
}
