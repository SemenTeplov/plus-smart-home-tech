package ru.yandex.practicum.dto;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record ShoppingCartDto(
        UUID shoppingCartId,
        Map<String, Long> products
) {

}
