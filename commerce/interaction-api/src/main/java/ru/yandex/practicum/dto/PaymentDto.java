package ru.yandex.practicum.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentDto(
        UUID paymentId,
        BigDecimal totalPayment,
        BigDecimal deliveryTotal,
        BigDecimal feeTotal
) {
}
