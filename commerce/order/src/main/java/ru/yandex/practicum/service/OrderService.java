package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

public interface OrderService {

    OrderDto getClientOrders(String username);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    ShoppingCartDto productReturn(ShoppingCartDto shoppingCartDto);

    OrderDto payment(String uuid);

    OrderDto paymentFailed(String uuid);

    OrderDto delivery(String uuid);

    OrderDto deliveryFailed(String uuid);

    OrderDto complete(String uuid);

    OrderDto calculateTotalCost(String uuid);

    OrderDto calculateDeliveryCost(String uuid);

    OrderDto assembly(String uuid);

    OrderDto assemblyFailed(String uuid);
}
