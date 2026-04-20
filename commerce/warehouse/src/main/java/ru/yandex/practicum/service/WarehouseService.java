package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
