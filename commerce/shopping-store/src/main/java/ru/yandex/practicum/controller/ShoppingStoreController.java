package ru.yandex.practicum.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;

@RestController("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @Valid @RequestParam(value = "category") String category,
            @Valid @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Valid @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Valid @RequestParam(value = "sort") List<Sort.Order> sort) {
        return ResponseEntity.ok(shoppingStoreService.getProducts(category, page, size, sort));
    }

    @PutMapping
    public ResponseEntity<ProductDto> createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(shoppingStoreService.createNewProduct(productDto));
    }

    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(shoppingStoreService.updateProduct(productDto));
    }

    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProductFromStore(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(shoppingStoreService.removeProductFromStore(uuid));
    }

    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @Valid @RequestBody SetProductQuantityStateRequest setProductQuantityStateRequest) {
        return ResponseEntity.ok(shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @Valid @PathVariable("productId") String productId, @Valid @RequestBody String uuid) {
        return ResponseEntity.ok(shoppingStoreService.getProduct(productId, uuid));
    }
}
