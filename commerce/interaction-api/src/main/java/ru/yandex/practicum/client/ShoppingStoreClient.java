package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ru.yandex.practicum.dto.ProductDto;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId);
}
