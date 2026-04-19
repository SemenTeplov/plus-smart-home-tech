package ru.yandex.practicum.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(
            @Valid @RequestParam(value = "username") String username) {
        return ResponseEntity.ok(shoppingCartService.getShoppingCart(username));
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @Valid @RequestParam(value = "username") String username,
            @Valid @RequestBody Map<String, Long> products) {
        return ResponseEntity.ok(shoppingCartService.addProductToShoppingCart(username, products));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(
            @Valid @RequestParam(value = "username") String username) {
        shoppingCartService.deactivateCurrentShoppingCart(username);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @Valid @RequestParam(value = "username") String username,
            @Valid @RequestBody List<UUID> products) {
        return ResponseEntity.ok(shoppingCartService.removeFromShoppingCart(username, products));
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @Valid @RequestParam(value = "username") String username,
            @Valid @RequestBody ChangeProductQuantityRequest changeProductQuantityRequest) {
        return ResponseEntity.ok(shoppingCartService.changeProductQuantity(username, changeProductQuantityRequest));
    }
}
