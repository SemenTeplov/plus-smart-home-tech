package ru.yandex.practicum.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PutMapping
    public ResponseEntity<Void> newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest request) {
        warehouseService.newProductInWarehouse(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/shipped")
    public ResponseEntity<Void> shippedToDelivery(
            @Valid @RequestBody ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> acceptReturn(
            @Valid @RequestBody Map<UUID, Long> request) {
        warehouseService.acceptReturn(request);

        return ResponseEntity.ok().build();
    }
  
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        return ResponseEntity.ok(warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCartDto));
    }

    @PostMapping("/assembly")
    public ResponseEntity<BookedProductsDto> assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return ResponseEntity.ok(warehouseService.assemblyProductsForOrder(request));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        return ResponseEntity.ok(warehouseService.getWarehouseAddress());
    }
}
