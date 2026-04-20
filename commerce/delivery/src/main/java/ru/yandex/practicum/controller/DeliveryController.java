package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto dto) {
        return ResponseEntity.ok(deliveryService.planDelivery(dto));
    }

    @PostMapping("/successful")
    ResponseEntity<Void> deliverySuccessful(@RequestBody UUID id) {
        deliveryService.deliverySuccessful(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/picked")
    ResponseEntity<Void> deliveryPicked(@RequestBody UUID id) {
        deliveryService.deliveryPicked(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/failed")
    ResponseEntity<Void> deliveryFailed(@RequestBody UUID id) {
        deliveryService.deliveryFailed(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cost")
    ResponseEntity<Double> deliveryCost(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(deliveryService.deliveryCost(orderDto));
    }
}
