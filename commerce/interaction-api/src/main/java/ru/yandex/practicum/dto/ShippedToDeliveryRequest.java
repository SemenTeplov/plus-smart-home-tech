package ru.yandex.practicum.dto;

import java.util.UUID;

public record ShippedToDeliveryRequest(
        UUID orderId,
        UUID deliveryId
) {
}
