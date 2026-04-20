package ru.yandex.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    ResponseEntity<OrderDto> getClientOrders(@Param("username") String username) {
        return ResponseEntity.ok(orderService.getClientOrders(username));
    }

    @PutMapping
    ResponseEntity<OrderDto> createNewOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        return ResponseEntity.ok(orderService.createNewOrder(request));
    }

    @PostMapping("/return")
    ResponseEntity<ShoppingCartDto> productReturn(@Param("productReturnRequest") ShoppingCartDto shoppingCartDto) {
        return ResponseEntity.ok(orderService.productReturn(shoppingCartDto));
    }

    @PostMapping("/payment")
    ResponseEntity<OrderDto> payment(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.payment(uuid));
    }

    @PostMapping("/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.paymentFailed(uuid));
    }

    @PostMapping("/delivery")
    ResponseEntity<OrderDto> delivery(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.delivery(uuid));
    }

    @PostMapping("/delivery/failed")
    ResponseEntity<OrderDto> deliveryFailed(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.deliveryFailed(uuid));
    }

    @PostMapping("/complete")
    ResponseEntity<OrderDto> complete(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.complete(uuid));
    }

    @PostMapping("/calculate/total")
    ResponseEntity<OrderDto> calculateTotalCost(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.calculateTotalCost(uuid));
    }

    @PostMapping("/calculate/delivery")
    ResponseEntity<OrderDto> calculateDeliveryCost(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.calculateDeliveryCost(uuid));
    }

    @PostMapping("/calculate/assembly")
    ResponseEntity<OrderDto> assembly(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.assembly(uuid));
    }

    @PostMapping("/calculate/assembly/failed")
    ResponseEntity<OrderDto> assemblyFailed(@Valid @RequestBody String uuid) {
        return ResponseEntity.ok(orderService.assemblyFailed(uuid));
    }
}
