package ru.yandex.practicum.dto;

import ru.yandex.practicum.persistence.enums.QuantityState;

public record SetProductQuantityStateRequest(
        String productId,
        QuantityState quantityState
) {
    @Override
    public String toString() {
        return String.format("id: %s, quantity state: %s", productId, quantityState.name());
    }
}
