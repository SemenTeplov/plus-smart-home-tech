package ru.yandex.practicum.dto;

import lombok.Builder;

import ru.yandex.practicum.status.DeliveryState;

import java.util.UUID;

@Builder
public record DeliveryDto(
        UUID deliveryId,
        AddressDto fromAddress,
        AddressDto toAddress,
        UUID orderId,
        DeliveryState deliveryState
) {
}
