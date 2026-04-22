package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto dto);

    void deliverySuccessful(UUID id);

    void deliveryPicked(UUID id);

    void deliveryFailed(UUID id);

    Double deliveryCost(OrderDto orderDto);
}
