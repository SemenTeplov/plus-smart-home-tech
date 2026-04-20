package ru.yandex.practicum.client;

import jakarta.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(
            @RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/assembly")
    ResponseEntity<BookedProductsDto> assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest request);

    @GetMapping("/address")
    ResponseEntity<AddressDto> getWarehouseAddress();

    @PostMapping("/shipped")
    ResponseEntity<Void> shippedToDelivery(
            @Valid @RequestBody ShippedToDeliveryRequest request);
}
