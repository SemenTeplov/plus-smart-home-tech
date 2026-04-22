package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import ru.yandex.practicum.status.State;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

    UUID orderId;

    UUID shoppingCartId;

    Map<UUID, Long> products;

    UUID paymentId;

    UUID deliveryId;

    State state;

    Double deliveryWeight;

    Double deliveryVolume;

    Boolean fragile;

    BigDecimal totalPrice;

    BigDecimal deliveryPrice;

    BigDecimal productPrice;
}
