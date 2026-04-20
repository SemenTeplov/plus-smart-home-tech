package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(paymentService.payment(orderDto));
    }

    @PostMapping("/totalCost")
    ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(paymentService.getTotalCost(orderDto));
    }

    @PostMapping("/refund")
    ResponseEntity<Void> paymentSuccess(@RequestBody UUID uuid) {
        paymentService.paymentSuccess(uuid);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/productCost")
    ResponseEntity<Double> productCost(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(paymentService.productCost(orderDto));
    }

    @PostMapping("/failed")
    ResponseEntity<Void> paymentFailed(@RequestBody UUID uuid) {
        paymentService.paymentFailed(uuid);

        return ResponseEntity.ok().build();
    }
}
