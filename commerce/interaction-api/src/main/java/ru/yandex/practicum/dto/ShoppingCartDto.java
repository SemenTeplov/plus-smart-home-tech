package ru.yandex.practicum.dto;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto(
        UUID shoppingCartId,
        Map<String, Long> products
) {

}
