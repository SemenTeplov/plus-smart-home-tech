package ru.yandex.practicum.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentDto(
        UUID paymentId,
        Double totalPayment,
        Double deliveryTotal,
        Double feeTotal
) {
}
