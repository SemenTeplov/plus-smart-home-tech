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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.persistence.entity.Product;
import ru.yandex.practicum.persistence.enums.QuantityState;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.util.StringToSortOrderConverter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    private final StringToSortOrderConverter converter;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(value = "category") String category,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "productName,ASC") List<String> sort) {
        return ResponseEntity.ok(shoppingStoreService.getProducts(
                category, page, size, sort.stream().map(converter::convert).toList()));
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
    public ResponseEntity<Boolean> removeProductFromStore(@Valid @RequestBody UUID uuid) {
        return ResponseEntity.ok(shoppingStoreService.removeProductFromStore(uuid));
    }

    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam(value = "productId") String productId,
            @RequestParam(value = "quantityState") QuantityState quantityState) {
        SetProductQuantityStateRequest setProductQuantityStateRequest =
                new SetProductQuantityStateRequest(productId, quantityState);

        return ResponseEntity.ok(shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable("productId") UUID productId) {
        return ResponseEntity.ok(shoppingStoreService.getProduct(productId));
    }
}
