package ru.yandex.practicum.dto;

public record CreateNewOrderRequest(
        ShoppingCartDto shoppingCart,
        AddressDto deliveryAddress
) {
}
